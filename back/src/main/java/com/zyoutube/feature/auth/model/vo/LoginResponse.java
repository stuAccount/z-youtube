package com.zyoutube.feature.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String bio;
}