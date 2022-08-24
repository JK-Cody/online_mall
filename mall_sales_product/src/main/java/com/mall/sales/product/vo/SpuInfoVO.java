/**
  * Copyright 2021 bejson.com 
  */
package com.mall.sales.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuInfoVO {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;  //重量数值对象
    private List<BaseAttrs> baseAttrs;  //规格参数列表
    private List<Skus> skus;  //sku列表

}