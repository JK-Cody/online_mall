package com.mall.sales.houseware.vo;

import lombok.Data;

import java.util.List;

/**
 * sku的库存信息
 */
@Data
public class SkuStockVO {

    private Long skuId;
    private List<Long> housewareIdList; //仓库Id列表
    private Integer number;
}
