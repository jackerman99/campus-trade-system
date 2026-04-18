package com.campus.trade.item.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemDbTestController {

    private final JdbcTemplate jdbcTemplate;

    public ItemDbTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/items/db-ping")
    public String dbPing() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return result != null && result == 1 ? "item-service db ok" : "item-service db fail";
    }
}