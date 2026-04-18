package com.campus.trade.admin.service;

import com.campus.trade.admin.dto.response.ItemAuditResponse;
import com.campus.trade.admin.dto.response.PendingItemResponse;
import com.campus.trade.common.response.PageResponse;

public interface AdminService {

    PageResponse<PendingItemResponse> getPendingItems(Integer pageNum, Integer pageSize);

    ItemAuditResponse approveItem(Long itemId, Long auditorId);

    ItemAuditResponse rejectItem(Long itemId, Long auditorId, String reason);
}