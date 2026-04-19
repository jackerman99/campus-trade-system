package com.campus.trade.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.PageResponse;
import com.campus.trade.item.dto.request.ItemListRequest;
import com.campus.trade.item.dto.request.ItemPublishRequest;
import com.campus.trade.item.dto.request.ItemSearchRequest;
import com.campus.trade.item.dto.response.ItemDetailResponse;
import com.campus.trade.item.dto.response.ItemListItemResponse;
import com.campus.trade.item.dto.response.ItemPublishResponse;
import com.campus.trade.item.dto.response.MyItemListItemResponse;
import com.campus.trade.item.entity.Category;
import com.campus.trade.item.entity.Item;
import com.campus.trade.item.entity.ItemImage;
import com.campus.trade.item.entity.SellerUser;
import com.campus.trade.item.mapper.CategoryMapper;
import com.campus.trade.item.mapper.ItemImageMapper;
import com.campus.trade.item.mapper.ItemMapper;
import com.campus.trade.item.mapper.SellerUserMapper;
import com.campus.trade.item.service.ItemService;
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
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final CategoryMapper categoryMapper;
    private final ItemImageMapper itemImageMapper;
    private final SellerUserMapper sellerUserMapper;

    public ItemServiceImpl(ItemMapper itemMapper,
                           CategoryMapper categoryMapper,
                           ItemImageMapper itemImageMapper,
                           SellerUserMapper sellerUserMapper) {
        this.itemMapper = itemMapper;
        this.categoryMapper = categoryMapper;
        this.itemImageMapper = itemImageMapper;
        this.sellerUserMapper = sellerUserMapper;
    }

    @Override
    public PageResponse<ItemListItemResponse> getItemPage(ItemListRequest request) {
        long pageNum = request.getPageNum() == null || request.getPageNum() < 1 ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null || request.getPageSize() < 1 ? 10L : request.getPageSize();

        if (pageSize > 50) {
            pageSize = 50;
        }

        LambdaQueryWrapper<Item> queryWrapper = buildBaseVisibleItemQuery();
        if (request.getCategoryId() != null) {
            queryWrapper.eq(Item::getCategoryId, request.getCategoryId());
        }

        Page<Item> page = new Page<>(pageNum, pageSize);
        Page<Item> itemPage = itemMapper.selectPage(page, queryWrapper);

        return buildItemPageResponse(itemPage);
    }

    @Override
    public ItemDetailResponse getItemDetail(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new BusinessException(400, "商品ID不合法");
        }

        LambdaQueryWrapper<Item> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(Item::getId, itemId)
                .eq(Item::getDeleted, 0)
                .eq(Item::getAuditStatus, "APPROVED")
                .eq(Item::getStatus, "ON_SALE");

        Item item = itemMapper.selectOne(itemQueryWrapper);
        if (item == null) {
            throw new BusinessException(404, "商品不存在");
        }

        Category category = item.getCategoryId() == null ? null : categoryMapper.selectById(item.getCategoryId());
        SellerUser sellerUser = item.getSellerId() == null ? null : sellerUserMapper.selectById(item.getSellerId());

        LambdaQueryWrapper<ItemImage> imageQueryWrapper = new LambdaQueryWrapper<>();
        imageQueryWrapper.eq(ItemImage::getItemId, item.getId())
                .orderByAsc(ItemImage::getSortNo)
                .orderByAsc(ItemImage::getId);

        List<ItemImage> itemImages = itemImageMapper.selectList(imageQueryWrapper);

        List<ItemDetailResponse.ItemImageInfo> imageInfos = itemImages.stream().map(image -> {
            ItemDetailResponse.ItemImageInfo imageInfo = new ItemDetailResponse.ItemImageInfo();
            imageInfo.setId(image.getId());
            imageInfo.setImageUrl(image.getImageUrl());
            imageInfo.setSortNo(image.getSortNo());
            return imageInfo;
        }).toList();

        ItemDetailResponse response = new ItemDetailResponse();
        response.setId(item.getId());
        response.setSellerId(item.getSellerId());
        response.setSellerNickname(sellerUser != null ? sellerUser.getNickname() : "未知");
        response.setCreditScore(100);

        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setCategoryId(item.getCategoryId());
        response.setCategoryName(category != null ? category.getName() : null);
        response.setConditionLevel(item.getConditionLevel());
        response.setTradeLocation(item.getTradeLocation());
        response.setCoverImage(item.getCoverImage());
        response.setStatus(item.getStatus());
        response.setAuditStatus(item.getAuditStatus());
        response.setAuditReason(item.getAuditReason());
        response.setPublishTime(item.getPublishTime());
        response.setImages(imageInfos);

        return response;
    }

    @Override
    public PageResponse<ItemListItemResponse> searchItems(ItemSearchRequest request) {
        long pageNum = request.getPageNum() == null || request.getPageNum() < 1 ? 1L : request.getPageNum();
        long pageSize = request.getPageSize() == null || request.getPageSize() < 1 ? 10L : request.getPageSize();

        if (pageSize > 50) {
            pageSize = 50;
        }

        LambdaQueryWrapper<Item> queryWrapper = buildBaseVisibleItemQuery();

        if (request.getCategoryId() != null) {
            queryWrapper.eq(Item::getCategoryId, request.getCategoryId());
        }

        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Item::getTitle, request.getKeyword())
                    .or()
                    .like(Item::getDescription, request.getKeyword()));
        }

        Page<Item> page = new Page<>(pageNum, pageSize);
        Page<Item> itemPage = itemMapper.selectPage(page, queryWrapper);

        return buildItemPageResponse(itemPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemPublishResponse publishItem(Long userId, ItemPublishRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(401, "未登录或登录状态无效");
        }

        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(400, "商品分类不存在");
        }
        if (!"ENABLED".equals(category.getStatus())) {
            throw new BusinessException(400, "商品分类不可用");
        }

        LocalDateTime now = LocalDateTime.now();

        Item item = new Item();
        item.setSellerId(userId);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategoryId(request.getCategoryId());
        item.setConditionLevel(request.getConditionLevel());
        item.setTradeLocation(request.getTradeLocation());
        item.setCoverImage(request.getCoverImage());
        item.setStatus("OFFLINE");
        item.setAuditStatus("PENDING");
        item.setAuditReason(null);
        item.setPublishTime(now);
        item.setDeleted(0);

        itemMapper.insert(item);

        List<String> imageUrls = request.getImages();
        if (imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = List.of(request.getCoverImage());
        }

        int sortNo = 1;
        for (String imageUrl : imageUrls) {
            if (!StringUtils.hasText(imageUrl)) {
                continue;
            }
            ItemImage itemImage = new ItemImage();
            itemImage.setItemId(item.getId());
            itemImage.setImageUrl(imageUrl);
            itemImage.setSortNo(sortNo++);
            itemImageMapper.insert(itemImage);
        }

        ItemPublishResponse response = new ItemPublishResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setStatus(item.getStatus());
        response.setAuditStatus(item.getAuditStatus());
        response.setPublishTime(item.getPublishTime());

        return response;
    }

    @Override
    public PageResponse<MyItemListItemResponse> getMyItems(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(401, "未登录或登录状态无效");
        }

        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        if (size > 50) {
            size = 50;
        }

        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Item::getSellerId, userId)
                .eq(Item::getDeleted, 0)
                .orderByDesc(Item::getPublishTime);

        Page<Item> page = new Page<>(current, size);
        Page<Item> itemPage = itemMapper.selectPage(page, queryWrapper);

        List<Item> itemList = itemPage.getRecords();

        Set<Long> categoryIds = itemList.stream()
                .map(Item::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<Long, String> categoryNameMap;
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        } else {
            categoryNameMap = Collections.emptyMap();
        }

        List<MyItemListItemResponse> records = itemList.stream().map(item -> {
            MyItemListItemResponse response = new MyItemListItemResponse();
            response.setId(item.getId());
            response.setTitle(item.getTitle());
            response.setPrice(item.getPrice());
            response.setCategoryId(item.getCategoryId());
            response.setCategoryName(categoryNameMap.get(item.getCategoryId()));
            response.setConditionLevel(item.getConditionLevel());
            response.setTradeLocation(item.getTradeLocation());
            response.setCoverImage(item.getCoverImage());
            response.setStatus(item.getStatus());
            response.setAuditStatus(item.getAuditStatus());
            response.setAuditReason(item.getAuditReason());
            response.setPublishTime(item.getPublishTime());
            return response;
        }).toList();

        PageResponse<MyItemListItemResponse> result = new PageResponse<>();
        result.setTotal(itemPage.getTotal());
        result.setPageNum(itemPage.getCurrent());
        result.setPageSize(itemPage.getSize());
        result.setTotalPages(itemPage.getPages());
        result.setRecords(records);

        return result;
    }

    private LambdaQueryWrapper<Item> buildBaseVisibleItemQuery() {
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Item::getDeleted, 0)
                .eq(Item::getAuditStatus, "APPROVED")
                .eq(Item::getStatus, "ON_SALE")
                .orderByDesc(Item::getPublishTime);
        return queryWrapper;
    }

    private PageResponse<ItemListItemResponse> buildItemPageResponse(Page<Item> itemPage) {
        List<Item> itemList = itemPage.getRecords();

        Set<Long> categoryIds = itemList.stream()
                .map(Item::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<Long, String> categoryNameMap;
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        } else {
            categoryNameMap = Collections.emptyMap();
        }

        List<ItemListItemResponse> responseList = itemList.stream().map(item -> {
            ItemListItemResponse response = new ItemListItemResponse();
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

        PageResponse<ItemListItemResponse> result = new PageResponse<>();
        result.setTotal(itemPage.getTotal());
        result.setPageNum(itemPage.getCurrent());
        result.setPageSize(itemPage.getSize());
        result.setTotalPages(itemPage.getPages());
        result.setRecords(responseList);

        return result;
    }
}