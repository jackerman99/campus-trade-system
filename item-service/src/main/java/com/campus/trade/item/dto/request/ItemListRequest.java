package com.campus.trade.item.dto.request;

import lombok.Data;

@Data
public class ItemListRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Long categoryId;
}