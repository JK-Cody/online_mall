package com.mall.sales.product.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Spu的属性介绍
 */
@ToString
@Data
public class AttrSimple {

    private Long attrId;
    private String attrName;
    private String attrValue;
}
