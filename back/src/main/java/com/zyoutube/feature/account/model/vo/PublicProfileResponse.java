package com.zyoutube.feature.account.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicProfileResponse {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String bio;
}
