package com.zyoutube.feature.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String username;
    private String email;
}
