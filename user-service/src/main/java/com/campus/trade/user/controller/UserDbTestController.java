package com.campus.trade.user.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDbTestController {

    private final JdbcTemplate jdbcTemplate;

    public UserDbTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/users/db-ping")
    public String dbPing() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return result != null && result == 1 ? "user-service db ok" : "user-service db fail";
    }
}