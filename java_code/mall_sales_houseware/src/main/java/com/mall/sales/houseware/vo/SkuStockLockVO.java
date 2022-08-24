package com.mall.sales.houseware.vo;

import lombok.Data;

import java.util.List;

/**
 * 订单创建后保存sku的库存信息
 */
@Data
public class SkuStockLockVO {

    private String orderSn; //订单号
    private List<CartItemVO> cartItemVOList; //购物车商品列表
}
