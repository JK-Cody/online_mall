/**
  * Copyright 2021 bejson.com 
  */
package com.mall.sales.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU复合对象
 */
@Data
public class Skus {

    private List<AttrSimple> attr;
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<Images> images;
    private List<String> descar;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}