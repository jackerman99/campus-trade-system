package com.campus.trade.admin.controller;

import com.campus.trade.admin.dto.request.ItemAuditRequest;
import com.campus.trade.admin.dto.response.ItemAuditResponse;
import com.campus.trade.admin.dto.response.PendingItemResponse;
import com.campus.trade.admin.service.AdminService;
import com.campus.trade.admin.util.JwtUtil;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.common.response.PageResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/admin/items/pending")
    public ApiResponse<PageResponse<PendingItemResponse>> getPendingItems(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        extractAdminUserIdFromAuthorization(authorization);

        Integer currentPage = pageNum != null ? pageNum : (page != null ? page : 1);
        Integer currentPageSize = pageSize != null ? pageSize : 10;

        return ApiResponse.success(adminService.getPendingItems(currentPage, currentPageSize));
    }

    @PostMapping("/api/admin/items/{id}/approve")
    public ApiResponse<ItemAuditResponse> approveItem(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("id") Long id,
            @RequestBody(required = false) ItemAuditRequest request) {

        Long adminUserId = extractAdminUserIdFromAuthorization(authorization);
        Long auditorId = request != null && request.getAuditorId() != null ? request.getAuditorId() : adminUserId;

        return ApiResponse.success("审核通过成功", adminService.approveItem(id, auditorId));
    }

    @PostMapping("/api/admin/items/{id}/reject")
    public ApiResponse<ItemAuditResponse> rejectItem(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("id") Long id,
            @RequestBody ItemAuditRequest request) {

        Long adminUserId = extractAdminUserIdFromAuthorization(authorization);
        Long auditorId = request != null && request.getAuditorId() != null ? request.getAuditorId() : adminUserId;
        String reason = request != null ? request.getReason() : null;

        return ApiResponse.success("审核驳回成功",
                adminService.rejectItem(id, auditorId, reason));
    }

    private Long extractAdminUserIdFromAuthorization(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "未提供登录令牌");
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "令牌格式错误");
        }

        String token = authorization.substring(7);

        Long userId;
        String role;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
            role = jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(401, "登录令牌无效");
        }

        if (userId == null) {
            throw new BusinessException(401, "登录令牌无效");
        }

        if (!"ADMIN".equals(role)) {
            throw new BusinessException(403, "无管理员权限");
        }

        return userId;
    }
}