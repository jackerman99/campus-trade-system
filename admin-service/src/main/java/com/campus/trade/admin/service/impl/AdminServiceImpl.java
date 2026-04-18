package com.campus.trade.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.admin.dto.response.ItemAuditResponse;
import com.campus.trade.admin.dto.response.PendingItemResponse;
import com.campus.trade.admin.entity.AdminCategory;
import com.campus.trade.admin.entity.AdminItem;
import com.campus.trade.admin.entity.AuditLog;
import com.campus.trade.admin.mapper.AdminCategoryMapper;
import com.campus.trade.admin.mapper.AdminItemMapper;
import com.campus.trade.admin.mapper.AuditLogMapper;
import com.campus.trade.admin.service.AdminService;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminItemMapper adminItemMapper;
    private final AdminCategoryMapper adminCategoryMapper;
    private final AuditLogMapper auditLogMapper;

    public AdminServiceImpl(AdminItemMapper adminItemMapper,
                            AdminCategoryMapper adminCategoryMapper,
                            AuditLogMapper auditLogMapper) {
        this.adminItemMapper = adminItemMapper;
        this.adminCategoryMapper = adminCategoryMapper;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public PageResponse<PendingItemResponse> getPendingItems(Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        if (size > 50) {
            size = 50;
        }

        LambdaQueryWrapper<AdminItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminItem::getDeleted, 0)
                .eq(AdminItem::getAuditStatus, "PENDING")
                .orderByDesc(AdminItem::getPublishTime);

        Page<AdminItem> page = new Page<>(current, size);
        Page<AdminItem> itemPage = adminItemMapper.selectPage(page, queryWrapper);

        List<AdminItem> itemList = itemPage.getRecords();

        Set<Long> categoryIds = itemList.stream()
                .map(AdminItem::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<Long, String> categoryNameMap;
        if (!categoryIds.isEmpty()) {
            List<AdminCategory> categories = adminCategoryMapper.selectBatchIds(categoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(AdminCategory::getId, AdminCategory::getName));
        } else {
            categoryNameMap = Collections.emptyMap();
        }

        List<PendingItemResponse> records = itemList.stream().map(item -> {
            PendingItemResponse response = new PendingItemResponse();
            response.setId(item.getId());
            response.setSellerId(item.getSellerId());
            response.setTitle(item.getTitle());
            response.setPrice(item.getPrice());
            response.setCategoryId(item.getCategoryId());
            response.setCategoryName(categoryNameMap.get(item.getCategoryId()));
            response.setConditionLevel(item.getConditionLevel());
            response.setTradeLocation(item.getTradeLocation());
            response.setCoverImage(item.getCoverImage());
            response.setStatus(item.getStatus());
            response.setAuditStatus(item.getAuditStatus());
            response.setPublishTime(item.getPublishTime());
            return response;
        }).toList();

        PageResponse<PendingItemResponse> result = new PageResponse<>();
        result.setTotal(itemPage.getTotal());
        result.setPageNum(itemPage.getCurrent());
        result.setPageSize(itemPage.getSize());
        result.setTotalPages(itemPage.getPages());
        result.setRecords(records);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemAuditResponse approveItem(Long itemId, Long auditorId) {
        AdminItem item = getPendingItem(itemId);

        item.setStatus("ON_SALE");
        item.setAuditStatus("APPROVED");
        item.setAuditReason(null);
        adminItemMapper.updateById(item);

        AuditLog auditLog = new AuditLog();
        auditLog.setItemId(item.getId());
        auditLog.setAuditorId(auditorId != null ? auditorId : 1L);
        auditLog.setAction("APPROVE");
        auditLog.setReason(null);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        ItemAuditResponse response = new ItemAuditResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setStatus(item.getStatus());
        response.setAuditStatus(item.getAuditStatus());
        response.setAuditReason(item.getAuditReason());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemAuditResponse rejectItem(Long itemId, Long auditorId, String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new BusinessException(400, "驳回原因不能为空");
        }

        AdminItem item = getPendingItem(itemId);

        item.setStatus("OFFLINE");
        item.setAuditStatus("REJECTED");
        item.setAuditReason(reason);
        adminItemMapper.updateById(item);

        AuditLog auditLog = new AuditLog();
        auditLog.setItemId(item.getId());
        auditLog.setAuditorId(auditorId != null ? auditorId : 1L);
        auditLog.setAction("REJECT");
        auditLog.setReason(reason);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        ItemAuditResponse response = new ItemAuditResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setStatus(item.getStatus());
        response.setAuditStatus(item.getAuditStatus());
        response.setAuditReason(item.getAuditReason());

        return response;
    }

    private AdminItem getPendingItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException(400, "商品ID不合法");
        }

        LambdaQueryWrapper<AdminItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminItem::getId, itemId)
                .eq(AdminItem::getDeleted, 0)
                .eq(AdminItem::getAuditStatus, "PENDING");

        AdminItem item = adminItemMapper.selectOne(queryWrapper);
        if (item == null) {
            throw new BusinessException(404, "待审核商品不存在");
        }
        return item;
    }
}