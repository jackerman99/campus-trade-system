package com.campus.trade.item.service.impl;

import com.campus.trade.common.ai.client.LlmClient;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.item.dto.request.ItemPolishRequest;
import com.campus.trade.item.dto.response.ItemPolishResponse;
import com.campus.trade.item.service.ItemAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ItemAiServiceImpl implements ItemAiService {

    private static final String SYSTEM_PROMPT = """
            你是校园二手交易平台的文案助手。
            你的任务是帮助用户润色商品标题和商品描述。

            要求：
            1. 保持原意，不得编造不存在的参数、品牌、成色或配件
            2. 语言自然、清晰、适合校园二手交易平台
            3. 标题更简洁明确，描述更完整通顺
            4. 不要夸张宣传，不要虚假承诺
            5. 只返回 JSON，不要返回任何解释文字
            6. JSON 字段固定为 polishedTitle 和 polishedDescription
            """;

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public ItemAiServiceImpl(LlmClient llmClient, ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ItemPolishResponse polish(ItemPolishRequest request) {
        String title = request.getTitle() == null ? "" : request.getTitle().trim();
        String description = request.getDescription() == null ? "" : request.getDescription().trim();

        if (!StringUtils.hasText(title) && !StringUtils.hasText(description)) {
            throw new BusinessException(400, "标题和描述不能同时为空");
        }

        String userPrompt = """
                请润色下面的商品信息，并严格按 JSON 返回。

                原始标题：
                %s

                原始描述：
                %s
                """.formatted(title, description);

        String aiContent = llmClient.chat(SYSTEM_PROMPT, userPrompt);
        JsonNode root = parseJson(aiContent);

        ItemPolishResponse response = new ItemPolishResponse();
        response.setPolishedTitle(readTextOrDefault(root, "polishedTitle", title));
        response.setPolishedDescription(readTextOrDefault(root, "polishedDescription", description));

        return response;
    }

    private JsonNode parseJson(String rawContent) {
        try {
            String cleaned = cleanMarkdownCodeBlock(rawContent);
            return objectMapper.readTree(cleaned);
        } catch (Exception e) {
            throw new BusinessException(502, "AI润色结果解析失败");
        }
    }

    private String cleanMarkdownCodeBlock(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = text.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }

    private String readTextOrDefault(JsonNode root, String fieldName, String defaultValue) {
        String value = root.path(fieldName).asText();
        return StringUtils.hasText(value) ? value.trim() : defaultValue;
    }
}