package com.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀商品的创建订单所需信息
 */
@Data
public class SeckillSkuCreateOrderTO {

    private Long memberId;
    private String orderSn;//订单号
    private Long skuId;
    private Long promotionSessionId;
    private Integer skuCount;
    private BigDecimal seckillPrice;
}
