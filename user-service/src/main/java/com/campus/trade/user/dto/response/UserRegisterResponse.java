package com.campus.trade.user.dto.response;

import lombok.Data;

@Data
public class UserRegisterResponse {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String role;
    private String status;
}