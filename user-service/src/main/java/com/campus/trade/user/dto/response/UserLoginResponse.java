package com.campus.trade.user.dto.response;

import lombok.Data;

@Data
public class UserLoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatarUrl;
        private String role;
        private String status;
    }
}