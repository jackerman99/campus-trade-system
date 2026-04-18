package com.campus.trade.item.controller;

import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.common.response.PageResponse;
import com.campus.trade.item.dto.request.ItemSearchRequest;
import com.campus.trade.item.dto.response.ItemListItemResponse;
import com.campus.trade.item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final ItemService itemService;

    public SearchController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/api/search/items")
    public ApiResponse<PageResponse<ItemListItemResponse>> searchItems(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {

        ItemSearchRequest request = new ItemSearchRequest();
        request.setKeyword(q != null ? q : keyword);
        request.setPageNum(pageNum != null ? pageNum : (page != null ? page : 1));
        request.setPageSize(pageSize != null ? pageSize : 10);
        request.setCategoryId(categoryId);

        return ApiResponse.success(itemService.searchItems(request));
    }
}