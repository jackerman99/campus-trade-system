package com.campus.trade.item.service;

import com.campus.trade.item.dto.request.ItemPolishRequest;
import com.campus.trade.item.dto.response.ItemPolishResponse;

public interface ItemAiService {

    ItemPolishResponse polish(ItemPolishRequest request);
}