package com.zyoutube.feature.auth;


import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.auth.model.dto.LoginRequest;
import com.zyoutube.feature.auth.model.vo.CurrentAccountResponse;
import com.zyoutube.feature.auth.model.vo.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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
    public ApiResponse<CurrentAccountResponse> me() {
        return ApiResponse.success(authService.getCurrentUser());
    }   


    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ApiResponse.success(null);
    }

}
