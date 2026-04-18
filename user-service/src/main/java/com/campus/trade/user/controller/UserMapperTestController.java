package com.campus.trade.user.controller;

import com.campus.trade.user.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserMapperTestController {

    private final UserMapper userMapper;

    public UserMapperTestController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/api/users/mapper-ping")
    public String mapperPing() {
        Long count = userMapper.selectCount(null);
        return "user-service mapper ok, user count = " + count;
    }
}