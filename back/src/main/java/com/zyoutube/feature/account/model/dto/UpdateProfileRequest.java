package com.zyoutube.feature.account.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private String nickname;

    private String bio;

    private String avatarUrl;

}
