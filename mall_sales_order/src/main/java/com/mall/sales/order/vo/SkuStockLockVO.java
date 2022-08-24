package com.mall.sales.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 订单创建后保存购物车商品列表和订单号
 */
@Data
public class SkuStockLockVO {

    private String orderSn;
    private List<CartItemVO> cartItemVOList;
}
