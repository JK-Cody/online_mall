package com.mall.sales.product.vo;

import lombok.Data;

/**
 * 保留 品牌id 和 品牌名
 */
@Data
public class BrandVO {

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名
     */
    private String brandName;
}
