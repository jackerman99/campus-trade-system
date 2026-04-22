package com.campus.trade.admin.controller;

import com.campus.trade.admin.dto.response.IntelligentReportResponse;
import com.campus.trade.admin.service.AdminReportService;
import com.campus.trade.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminReportController {

    private final AdminReportService adminReportService;

    public AdminReportController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    @GetMapping("/api/admin/reports/intelligent")
    public ApiResponse<IntelligentReportResponse> intelligentReport(
            @RequestParam(defaultValue = "7") Integer days) {
        return ApiResponse.success(adminReportService.generateIntelligentReport(days));
    }
}