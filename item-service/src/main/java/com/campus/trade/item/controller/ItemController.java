package com.campus.trade.item.controller;

import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.common.response.PageResponse;
import com.campus.trade.item.dto.request.ItemListRequest;
import com.campus.trade.item.dto.request.ItemPublishRequest;
import com.campus.trade.item.dto.response.ItemDetailResponse;
import com.campus.trade.item.dto.response.ItemListItemResponse;
import com.campus.trade.item.dto.response.ItemPublishResponse;
import com.campus.trade.item.dto.response.MyItemListItemResponse;
import com.campus.trade.item.service.ItemService;
import com.campus.trade.item.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    private final ItemService itemService;
    private final JwtUtil jwtUtil;

    public ItemController(ItemService itemService, JwtUtil jwtUtil) {
        this.itemService = itemService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/items")
    public ApiResponse<PageResponse<ItemListItemResponse>> getItemPage(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {

        ItemListRequest request = new ItemListRequest();
        request.setPageNum(pageNum != null ? pageNum : (page != null ? page : 1));
        request.setPageSize(pageSize != null ? pageSize : 10);
        request.setCategoryId(categoryId);

        return ApiResponse.success(itemService.getItemPage(request));
    }

    @GetMapping("/api/items/{id}")
    public ApiResponse<ItemDetailResponse> getItemDetail(@PathVariable("id") Long id) {
        return ApiResponse.success(itemService.getItemDetail(id));
    }

    @PostMapping("/api/items")
    public ApiResponse<ItemPublishResponse> publishItem(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ItemPublishRequest request) {

        Long userId = extractUserIdFromAuthorization(authorization);
        return ApiResponse.success("发布成功，等待审核", itemService.publishItem(userId, request));
    }

    @GetMapping("/api/items/mine")
    public ApiResponse<PageResponse<MyItemListItemResponse>> getMyItems(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Long userId = extractUserIdFromAuthorization(authorization);
        Integer currentPage = pageNum != null ? pageNum : (page != null ? page : 1);
        Integer currentPageSize = pageSize != null ? pageSize : 10;

        return ApiResponse.success(itemService.getMyItems(userId, currentPage, currentPageSize));
    }

    private Long extractUserIdFromAuthorization(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "未提供登录令牌");
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "令牌格式错误");
        }

        String token = authorization.substring(7);

        Long userId;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(401, "登录令牌无效");
        }

        if (userId == null) {
            throw new BusinessException(401, "登录令牌无效");
        }
        return userId;
    }
}