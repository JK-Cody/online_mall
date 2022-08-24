/**
  * Copyright 2021 bejson.com 
  */
package com.mall.sales.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 重量数值对象
 */
@Data
public class Bounds {

    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}