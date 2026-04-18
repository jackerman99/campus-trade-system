package com.campus.trade.item.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemPublishRequest {

    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题不能超过100个字符")
    private String title;

    @NotBlank(message = "商品描述不能为空")
    @Size(max = 2000, message = "商品描述不能超过2000个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "新旧程度不能为空")
    @Min(value = 1, message = "新旧程度最小为1")
    @Max(value = 5, message = "新旧程度最大为5")
    private Integer conditionLevel;

    @NotBlank(message = "交易地点不能为空")
    @Size(max = 100, message = "交易地点不能超过100个字符")
    private String tradeLocation;

    @NotBlank(message = "封面图片不能为空")
    @Size(max = 255, message = "封面图片地址不能超过255个字符")
    private String coverImage;

    private List<String> images;
}