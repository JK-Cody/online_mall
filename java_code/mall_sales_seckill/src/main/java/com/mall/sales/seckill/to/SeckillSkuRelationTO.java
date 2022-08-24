package com.mall.sales.seckill.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SeckillSkuRelationEntity的TO
 */
@Data
public class SeckillSkuRelationTO {

    private Long id;
    private Long promotionId;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer seckillCount;
    private Integer seckillLimit;
    private Integer seckillSort;

}
