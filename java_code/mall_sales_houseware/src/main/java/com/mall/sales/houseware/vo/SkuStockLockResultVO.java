package com.mall.sales.houseware.vo;

import lombok.Data;

/**
 * 锁定订单的sku库存的信息
 */
@Data
public class SkuStockLockResultVO {

    private Long skuId;
    private Integer number;
    private Boolean lockResult;
}
