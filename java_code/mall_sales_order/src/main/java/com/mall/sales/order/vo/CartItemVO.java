package com.mall.sales.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单的购物车内容
 */
@Data
public class CartItemVO {

    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttrList;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private boolean hasStock;
    private BigDecimal weight;
}
