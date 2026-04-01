package com.zyoutube.feature.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String username;
    private String email;
}
