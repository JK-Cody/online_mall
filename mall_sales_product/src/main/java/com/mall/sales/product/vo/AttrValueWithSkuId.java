package com.mall.sales.product.vo;

import lombok.Data;
/**
 * Spu的Id和销售属性
 */
@Data
public class AttrValueWithSkuId {

    private String attrValue;
    private String skuId;
}
