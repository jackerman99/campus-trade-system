package com.campus.trade.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.item.entity.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}