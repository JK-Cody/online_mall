package com.mall.sales.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀活动的单个sku内容
 */
@Data
public class SeckillSkuDetailVO {

    private long startTime;
    private Long endTime;
    private String ramdomCode; //随机码，防脚本请求

    private Long id;
    private Long promotionId;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer seckillCount;
    private Integer seckillLimit;
    private Integer seckillSort;
}
