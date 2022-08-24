package com.mall.sales.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.to.SeckillSkuCreateOrderTO;
import com.mall.common.utils.PageUtils;
import com.mall.sales.order.vo.*;
import com.mall.sales.order.entity.OrderEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 00:39:26
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVO saveCreatedOrder() throws ExecutionException, InterruptedException;

    ResponseOfCreatingOrderVO createOrderWithSubmittedData(OrderMarkInfoOfSubmittedDataVO orderMarkInfoOfSubmittedDataVO);

    OrderEntity getOrderByOrderSn(String orderSn);

    void confirmOrderClose(OrderEntity order);

    PageUtils returnOrderPage(Map<String, Object> params);

    String handleAliPaymentNotification(AliPaymentNotificationVO vo);

    void saveSeckillSkuCreateOrder(SeckillSkuCreateOrderTO createOrder);

    void handleWechaPaymentNotification(HashMap map,String out_trade_no);

    OrderEntity getOrderStatusByOrderSn(String orderSn);
}

