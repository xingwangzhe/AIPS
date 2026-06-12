package com.needhelp.aips.infrastructure.auth;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.dto.user.LoginRequest;
import com.needhelp.aips.dto.user.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口。
 * POST /api/v1/user/login
 */
@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);
        return ApiResponse.success(result);
    }
}
