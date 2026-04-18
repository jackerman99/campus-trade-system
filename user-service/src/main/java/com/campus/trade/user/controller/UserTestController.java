package com.campus.trade.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserTestController {

    @GetMapping("/api/users/ping")
    public String ping() {
        return "user-service ok";
    }

    @GetMapping("/api/auth/ping")
    public String authPing() {
        return "auth route ok";
    }
}