package com.campus.trade.admin.dto.response;

import lombok.Data;

@Data
public class IntelligentReportResponse {

    private String period;
    private ReportMetricsResponse metrics;
    private String aiSummary;
}