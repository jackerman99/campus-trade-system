package com.campus.trade.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminTestController {

    @GetMapping("/api/admin/ping")
    public String ping() {
        return "admin-service ok";
    }
}