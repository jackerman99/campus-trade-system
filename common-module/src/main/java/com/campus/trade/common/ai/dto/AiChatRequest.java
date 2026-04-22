package com.campus.trade.common.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {

    private String model;
    private List<AiMessage> messages;
    private Double temperature = 0.4;
}