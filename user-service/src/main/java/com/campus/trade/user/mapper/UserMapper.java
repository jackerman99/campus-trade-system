package com.campus.trade.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}