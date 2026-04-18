package com.campus.trade.item.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MyItemListItemResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer conditionLevel;
    private String tradeLocation;
    private String coverImage;
    private String status;
    private String auditStatus;
    private String auditReason;
    private LocalDateTime publishTime;
}