package com.campus.trade.user.controller;

import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserResponseTestController {

    @GetMapping("/api/users/response-ping")
    public ApiResponse<Map<String, Object>> responsePing() {
        return ApiResponse.success(Map.of(
                "service", "user-service",
                "status", "ok"
        ));
    }

    @GetMapping("/api/users/exception-ping")
    public ApiResponse<Void> exceptionPing() {
        throw new BusinessException(400, "这是一个测试业务异常");
    }
}