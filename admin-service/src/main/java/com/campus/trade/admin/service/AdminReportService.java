package com.campus.trade.admin.service;

import com.campus.trade.admin.dto.response.IntelligentReportResponse;

public interface AdminReportService {

    IntelligentReportResponse generateIntelligentReport(Integer days);
}