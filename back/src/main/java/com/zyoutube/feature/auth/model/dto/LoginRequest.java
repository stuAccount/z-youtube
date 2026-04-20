package com.zyoutube.feature.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * TODO Sprint 7: login request payload.
 */
@Getter
public class LoginRequest {
    @NotBlank(message = "Please enter email or username")
    private String loginId; // Email or username

    @NotBlank(message = "Please enter password")
    private String password;
}
