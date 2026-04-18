package com.campus.trade.admin.dto.response;

import lombok.Data;

@Data
public class ItemAuditResponse {

    private Long id;
    private String title;
    private String status;
    private String auditStatus;
    private String auditReason;
}