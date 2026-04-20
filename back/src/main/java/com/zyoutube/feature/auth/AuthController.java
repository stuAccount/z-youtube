package com.zyoutube.feature.auth;

/**
 * TODO Sprint 7: authentication endpoints such as login, logout, and me.
 */

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.account.AccountService;
import com.zyoutube.feature.auth.model.dto.LoginRequest;
import com.zyoutube.feature.auth.model.vo.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        return ApiResponse.success(authService.login(req, request, response));
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse> me() {
        return ApiResponse.success(authService.getCurrentUser());
    }   


    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success(null);
    }

}
