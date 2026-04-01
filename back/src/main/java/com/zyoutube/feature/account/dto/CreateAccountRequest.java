package com.zyoutube.feature.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class CreateAccountRequest {
    @NotBlank
    @Getter @Setter
    private String username;

    @Getter @Setter
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Getter @Setter
    private String password;

}