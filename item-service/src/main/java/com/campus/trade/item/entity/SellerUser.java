package com.campus.trade.item.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class SellerUser {

    @TableId
    private Long id;

    private String nickname;
}