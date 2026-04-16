package com.zyoutube.feature.account.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SelfProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String bio;
}
