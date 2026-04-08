package com.zyoutube.feature.account.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateAccountRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    /* TODO:
     * Modifying password not implemented here but a seperate dto?
     */
}
