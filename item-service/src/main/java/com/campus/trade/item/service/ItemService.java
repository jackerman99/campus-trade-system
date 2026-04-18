package com.campus.trade.item.service;

import com.campus.trade.common.response.PageResponse;
import com.campus.trade.item.dto.request.ItemListRequest;
import com.campus.trade.item.dto.request.ItemPublishRequest;
import com.campus.trade.item.dto.request.ItemSearchRequest;
import com.campus.trade.item.dto.response.ItemDetailResponse;
import com.campus.trade.item.dto.response.ItemListItemResponse;
import com.campus.trade.item.dto.response.ItemPublishResponse;
import com.campus.trade.item.dto.response.MyItemListItemResponse;

public interface ItemService {

    PageResponse<ItemListItemResponse> getItemPage(ItemListRequest request);

    ItemDetailResponse getItemDetail(Long itemId);

    PageResponse<ItemListItemResponse> searchItems(ItemSearchRequest request);

    ItemPublishResponse publishItem(Long userId, ItemPublishRequest request);

    PageResponse<MyItemListItemResponse> getMyItems(Long userId, Integer pageNum, Integer pageSize);
}