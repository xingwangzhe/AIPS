package com.needhelp.aips.controller;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.dto.user.HealthProfileResponse;
import com.needhelp.aips.dto.user.ReminderRequest;
import com.needhelp.aips.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户接口。
 * GET  /api/v1/user/health  - 获取健康档案
 * POST /api/v1/user/reminder - 设置用药提醒
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/health")
    public ApiResponse<HealthProfileResponse> getHealth(Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        HealthProfileResponse profile = userService.getHealthProfile(userId);
        return ApiResponse.success(profile);
    }

    @PostMapping("/reminder")
    public ApiResponse<Map<String, Object>> setReminder(@Valid @RequestBody ReminderRequest request,
                                                         Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        String message = userService.setReminder(userId, request);
        return ApiResponse.success(Map.of("message", message));
    }
}
