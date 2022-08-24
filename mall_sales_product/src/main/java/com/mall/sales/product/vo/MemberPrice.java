/**
  * Copyright 2021 bejson.com 
  */
package com.mall.sales.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SKU会员价对象
 */
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;


}