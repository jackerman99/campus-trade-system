package com.campus.trade.item.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItemListItemResponse {

    private Long id;
    private Long sellerId;
    private String title;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer conditionLevel;
    private String tradeLocation;
    private String coverImage;
    private String status;
    private String auditStatus;
    private LocalDateTime publishTime;
}