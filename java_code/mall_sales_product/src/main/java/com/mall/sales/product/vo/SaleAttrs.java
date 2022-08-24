package com.mall.sales.product.vo;

import lombok.Data;

import java.util.List;

/**
 * 销售属性
 */
@Data
public class SaleAttrs {

    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuId> attrValueWithSkuIds;
}
