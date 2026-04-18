package com.campus.trade.user.controller;

import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.user.dto.response.CurrentUserResponse;
import com.campus.trade.user.service.UserService;
import com.campus.trade.user.util.JwtUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/users/me")
    public ApiResponse<CurrentUserResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

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

        CurrentUserResponse response = userService.getCurrentUser(userId);
        return ApiResponse.success(response);
    }
}