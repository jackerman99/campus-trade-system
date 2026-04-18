package com.campus.trade.item.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemTestController {

    @GetMapping("/api/items/ping")
    public String itemPing() {
        return "item-service ok";
    }

    @GetMapping("/api/search/ping")
    public String searchPing() {
        return "search route ok";
    }

    @GetMapping("/api/files/ping")
    public String filePing() {
        return "file route ok";
    }
}