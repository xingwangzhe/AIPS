package com.needhelp.aips.infrastructure.auth;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.common.util.JwtUtil;
import com.needhelp.aips.dto.user.LoginRequest;
import com.needhelp.aips.dto.user.LoginResponse;
import com.needhelp.aips.entity.User;
import com.needhelp.aips.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证中心服务。
 * 处理用户登录、注册、JWT 令牌签发。
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 手机号 + 验证码登录。
     * 新用户自动注册。验证码在开发阶段固定为 "123456"。
     */
    public LoginResponse login(LoginRequest request) {
        // 验证码校验（开发阶段固定为 123456）
        if (!"123456".equals(request.getVerifyCode())) {
            throw new BusinessException(400, "验证码错误");
        }

        // 查找或创建用户
        User user = userRepository.findByPhone(request.getPhone())
                .orElseGet(() -> registerNewUser(request.getPhone()));

        // 检查用户状态
        if (user.getStatus() == 2) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (user.getStatus() == 3) {
            throw new BusinessException(403, "账号已注销");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 生成令牌
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        // 组装响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpireIn(7200);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNickname(user.getNickname() != null ? user.getNickname() : "");
        userInfo.setPhone(maskPhone(user.getPhone()));
        userInfo.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        response.setUserInfo(userInfo);

        return response;
    }

    private User registerNewUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode("default"));
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setStatus(1);
        user.setMemberLevel(1);
        user.setGender(0);
        return userRepository.save(user);
    }

    private String maskPhone(String phone) {
        if (phone != null && phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return phone;
    }
}
