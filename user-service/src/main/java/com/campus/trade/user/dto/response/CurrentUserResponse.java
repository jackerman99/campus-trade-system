package com.campus.trade.user.dto.response;

import lombok.Data;

@Data
public class CurrentUserResponse {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;
}