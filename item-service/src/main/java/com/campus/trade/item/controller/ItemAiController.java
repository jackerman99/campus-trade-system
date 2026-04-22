package com.campus.trade.item.controller;

import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.item.dto.request.ItemPolishRequest;
import com.campus.trade.item.dto.response.ItemPolishResponse;
import com.campus.trade.item.service.ItemAiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemAiController {

    private final ItemAiService itemAiService;

    public ItemAiController(ItemAiService itemAiService) {
        this.itemAiService = itemAiService;
    }

    @PostMapping("/api/items/ai/polish")
    public ApiResponse<ItemPolishResponse> polish(@Valid @RequestBody ItemPolishRequest request) {
        return ApiResponse.success(itemAiService.polish(request));
    }
}