package com.campus.trade.user.service;

import com.campus.trade.user.dto.request.UserLoginRequest;
import com.campus.trade.user.dto.request.UserRegisterRequest;
import com.campus.trade.user.dto.response.CurrentUserResponse;
import com.campus.trade.user.dto.response.UserLoginResponse;
import com.campus.trade.user.dto.response.UserRegisterResponse;

public interface UserService {

    UserRegisterResponse register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

    CurrentUserResponse getCurrentUser(Long userId);
}