package com.campus.trade.common.ai.client;

import com.campus.trade.common.ai.dto.AiChatRequest;
import com.campus.trade.common.ai.dto.AiMessage;
import com.campus.trade.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Component
public class LlmClient {

    private final ObjectMapper objectMapper;

    @Value("${ai.enabled:false}")
    private boolean enabled;

    @Value("${ai.chat-url:}")
    private String chatUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:}")
    private String model;

    @Value("${ai.timeout-seconds:30}")
    private int timeoutSeconds;

    public LlmClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String chat(List<AiMessage> messages) {
        if (!enabled) {
            throw new BusinessException(503, "AI服务未启用");
        }
        if (!StringUtils.hasText(chatUrl) || !StringUtils.hasText(apiKey) || !StringUtils.hasText(model)) {
            throw new BusinessException(503, "AI服务配置不完整");
        }

        AiChatRequest requestBody = new AiChatRequest();
        requestBody.setModel(model);
        requestBody.setMessages(messages);

        try {
            String json = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(chatUrl))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(502, "AI服务调用失败，HTTP状态码：" + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");

            if (contentNode.isMissingNode() || !StringUtils.hasText(contentNode.asText())) {
                throw new BusinessException(502, "AI服务返回内容为空");
            }

            return contentNode.asText();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(502, "AI服务调用异常：" + e.getMessage());
        }
    }

    public String chat(String systemPrompt, String userPrompt) {
        return chat(List.of(
                new AiMessage("system", systemPrompt),
                new AiMessage("user", userPrompt)
        ));
    }
}