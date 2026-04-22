package com.campus.trade.admin.service.impl;

import com.campus.trade.admin.dto.response.IntelligentReportResponse;
import com.campus.trade.admin.dto.response.ReportMetricsResponse;
import com.campus.trade.admin.service.AdminReportService;
import com.campus.trade.common.ai.client.LlmClient;
import com.campus.trade.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminReportServiceImpl implements AdminReportService {

    private static final String SYSTEM_PROMPT = """
            你是校园二手交易平台的数据分析助手。
            请根据给定的结构化运营数据，生成一段简洁、专业、适合课程项目展示的运营分析总结。
            
            要求：
            1. 总结控制在100~180字
            2. 语言自然，像管理员后台报表摘要
            3. 强调平台活跃度、审核情况、热门分类和可改进点
            4. 不要编造数据
            5. 只返回纯文本，不要返回 JSON
            """;

    private final JdbcTemplate jdbcTemplate;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public AdminReportServiceImpl(JdbcTemplate jdbcTemplate, LlmClient llmClient, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public IntelligentReportResponse generateIntelligentReport(Integer days) {
        if (days == null) {
            days = 7;
        }
        if (days < 1 || days > 365) {
            throw new BusinessException(400, "days 取值范围应为 1~365");
        }

        ReportMetricsResponse metrics = buildMetrics(days);

        IntelligentReportResponse response = new IntelligentReportResponse();
        response.setPeriod("最近" + days + "天");
        response.setMetrics(metrics);
        response.setAiSummary(buildAiSummary(days, metrics));

        return response;
    }

    private ReportMetricsResponse buildMetrics(Integer days) {
        ReportMetricsResponse metrics = new ReportMetricsResponse();

        metrics.setPublishedItems(queryCount("""
                SELECT COUNT(*)
                FROM items
                WHERE publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                """, days));

        metrics.setApprovedItems(queryCount("""
                SELECT COUNT(*)
                FROM items
                WHERE publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                  AND audit_status = 'APPROVED'
                """, days));

        metrics.setRejectedItems(queryCount("""
                SELECT COUNT(*)
                FROM items
                WHERE publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                  AND audit_status = 'REJECTED'
                """, days));

        metrics.setPendingItems(queryCount("""
                SELECT COUNT(*)
                FROM items
                WHERE publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                  AND audit_status = 'PENDING'
                """, days));

        metrics.setAveragePrice(queryDouble("""
                SELECT AVG(price)
                FROM items
                WHERE publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                """, days));

        metrics.setTopCategory(queryString("""
                SELECT c.name
                FROM items i
                LEFT JOIN categories c ON i.category_id = c.id
                WHERE i.publish_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                GROUP BY i.category_id, c.name
                ORDER BY COUNT(*) DESC
                LIMIT 1
                """, days, "暂无数据"));

        return metrics;
    }

    private String buildAiSummary(Integer days, ReportMetricsResponse metrics) {
        try {
            String metricsJson = objectMapper.writeValueAsString(metrics);

            String userPrompt = """
                    请根据以下最近%d天的运营数据生成一段平台运营分析摘要：
                    
                    %s
                    """.formatted(days, metricsJson);

            String summary = llmClient.chat(SYSTEM_PROMPT, userPrompt);
            if (StringUtils.hasText(summary)) {
                return summary.trim();
            }
        } catch (Exception ignored) {
        }

        return buildFallbackSummary(days, metrics);
    }

    private String buildFallbackSummary(Integer days, ReportMetricsResponse metrics) {
        return "最近" + days + "天平台共发布商品" + safeInt(metrics.getPublishedItems()) + "件，"
                + "其中审核通过" + safeInt(metrics.getApprovedItems()) + "件，"
                + "驳回" + safeInt(metrics.getRejectedItems()) + "件，"
                + "待审核" + safeInt(metrics.getPendingItems()) + "件。"
                + "当前最热门分类为“" + safeText(metrics.getTopCategory()) + "”，"
                + "商品平均价格约为" + safeDouble(metrics.getAveragePrice()) + "元，"
                + "整体运行情况较为平稳。";
    }

    private Integer queryCount(String sql, Object param) {
        try {
            Integer value = jdbcTemplate.queryForObject(sql, Integer.class, param);
            return value == null ? 0 : value;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    private Double queryDouble(String sql, Object param) {
        try {
            Double value = jdbcTemplate.queryForObject(sql, Double.class, param);
            return value == null ? 0.0 : Math.round(value * 100.0) / 100.0;
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    private String queryString(String sql, Object param, String defaultValue) {
        try {
            String value = jdbcTemplate.queryForObject(sql, String.class, param);
            return StringUtils.hasText(value) ? value : defaultValue;
        } catch (EmptyResultDataAccessException e) {
            return defaultValue;
        }
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private String safeText(String value) {
        return StringUtils.hasText(value) ? value : "暂无数据";
    }
}