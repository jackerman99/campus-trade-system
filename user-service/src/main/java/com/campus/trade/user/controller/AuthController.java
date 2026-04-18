package com.campus.trade.user.controller;

import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.user.dto.request.UserLoginRequest;
import com.campus.trade.user.dto.request.UserRegisterRequest;
import com.campus.trade.user.dto.response.UserLoginResponse;
import com.campus.trade.user.dto.response.UserRegisterResponse;
import com.campus.trade.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse response = userService.register(request);
        return ApiResponse.success("注册成功", response);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ApiResponse.success("登录成功", response);
    }
}