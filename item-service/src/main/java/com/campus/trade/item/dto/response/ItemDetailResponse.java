package com.campus.trade.item.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDetailResponse {

    private Long id;
    private Long sellerId;
    private String sellerNickname;
    private Integer creditScore;

    private String title;
    private String description;
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
    private List<ItemImageInfo> images;

    @Data
    public static class ItemImageInfo {
        private Long id;
        private String imageUrl;
        private Integer sortNo;
    }
}