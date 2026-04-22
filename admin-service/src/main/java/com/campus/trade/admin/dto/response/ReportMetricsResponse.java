package com.campus.trade.admin.dto.response;

import lombok.Data;

@Data
public class ReportMetricsResponse {

    private Integer publishedItems;
    private Integer approvedItems;
    private Integer rejectedItems;
    private Integer pendingItems;
    private Double averagePrice;
    private String topCategory;
}