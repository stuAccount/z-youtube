package com.zyoutube.feature.account.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @NotBlank(message = "Old password can not be blank")
    private String oldPassword;

    @NotBlank(message = "New password can not be blank")
    @Size(min = 8, max = 16, message = "Password's length must between 8 and 16")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password can not be blank")
    private String confirmPassword;
}