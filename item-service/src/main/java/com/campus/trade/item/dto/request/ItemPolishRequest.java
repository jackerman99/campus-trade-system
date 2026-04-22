package com.campus.trade.item.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemPolishRequest {

    @Size(max = 100, message = "商品标题不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "商品描述不能超过2000个字符")
    private String description;
}