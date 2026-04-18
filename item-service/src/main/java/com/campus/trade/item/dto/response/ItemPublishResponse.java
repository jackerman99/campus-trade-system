package com.campus.trade.item.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemPublishResponse {

    private Long id;
    private String title;
    private String status;
    private String auditStatus;
    private LocalDateTime publishTime;
}