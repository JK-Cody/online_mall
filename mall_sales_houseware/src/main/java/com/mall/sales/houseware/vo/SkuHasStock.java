package com.mall.sales.houseware.vo;

import lombok.Data;

/**
 * 判断Sku库存是否存在
 */
@Data
public class SkuHasStock {

    private Long skuId;
    private Boolean hasStock;
}
