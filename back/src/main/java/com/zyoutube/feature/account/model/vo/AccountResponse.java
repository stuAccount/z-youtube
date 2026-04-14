package com.zyoutube.feature.account.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String username;
    private String email;
}
