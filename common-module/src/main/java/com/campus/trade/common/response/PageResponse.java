package com.campus.trade.common.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Long totalPages;
    private List<T> records;
}