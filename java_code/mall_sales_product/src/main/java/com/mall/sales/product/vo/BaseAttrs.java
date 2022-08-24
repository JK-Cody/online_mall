/**
  * Copyright 2021 bejson.com 
  */
package com.mall.sales.product.vo;

import lombok.Data;

/**
 * Spu的规格参数列表
 */
@Data
public class BaseAttrs {

    private Long attrId;
    private String attrValues;
    private int showDesc;
}