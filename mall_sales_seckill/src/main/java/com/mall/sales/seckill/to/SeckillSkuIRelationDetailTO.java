package com.mall.sales.seckill.to;

import com.mall.sales.seckill.vo.SkuInfoVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SeckillSkuRelationTO的详细内容
 */
@Data
public class SeckillSkuIRelationDetailTO {

    private SkuInfoVO skuInfo;
    private long startTime;
    private Long endTime;
    private String ramdomCode; //随机码，防止秒杀订单重复提交

    private Long id;
    private Long promotionId;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer seckillCount;
    private Integer seckillLimit;
    private Integer seckillSort;

}
