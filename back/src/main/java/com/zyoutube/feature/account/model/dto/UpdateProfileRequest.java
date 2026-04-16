package com.zyoutube.feature.account.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    // ===current business logic: username is fixed after registration===

    @Email
    private String email;

    @Size(max = 50)
    private String nickname;

    @Size(max = 500)
    private String avatarUrl;

    @Size(max = 500)
    private String bio;

}
