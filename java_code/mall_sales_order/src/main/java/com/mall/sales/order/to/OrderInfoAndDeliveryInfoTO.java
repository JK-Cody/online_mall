package com.mall.sales.order.to;

import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.entity.OrderItemEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 保存前端的订单数据
 */
@Data
public class OrderInfoAndDeliveryInfoTO implements Serializable {
    //订单的内容
    private OrderEntity order;
    //订单的商品内容
    private List<OrderItemEntity> orderItemList;
    //订单支付总价
    private BigDecimal payPrice;
    //订单支付运费
    private BigDecimal deliveryFare;
}
