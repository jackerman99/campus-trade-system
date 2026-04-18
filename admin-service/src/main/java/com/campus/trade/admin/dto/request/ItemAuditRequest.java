package com.campus.trade.admin.dto.request;

import lombok.Data;

@Data
public class ItemAuditRequest {

    private Long auditorId;
    private String reason;
}