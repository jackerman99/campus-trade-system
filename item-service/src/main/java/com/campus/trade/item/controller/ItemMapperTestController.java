package com.campus.trade.item.controller;

import com.campus.trade.item.mapper.CategoryMapper;
import com.campus.trade.item.mapper.ItemMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ItemMapperTestController {

    private final ItemMapper itemMapper;
    private final CategoryMapper categoryMapper;

    public ItemMapperTestController(ItemMapper itemMapper, CategoryMapper categoryMapper) {
        this.itemMapper = itemMapper;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/api/items/mapper-ping")
    public Map<String, Object> mapperPing() {
        Long itemCount = itemMapper.selectCount(null);
        Long categoryCount = categoryMapper.selectCount(null);

        return Map.of(
                "service", "item-service",
                "itemCount", itemCount,
                "categoryCount", categoryCount
        );
    }
}