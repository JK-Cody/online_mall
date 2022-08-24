package com.mall.sales.order.service.impl;

import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.entity.OrderItemEntity;
import com.mall.sales.order.service.OrderItemService;
import com.mall.sales.order.service.OrderService;
import com.mall.sales.order.vo.AliPaymentVO;
import com.mall.sales.order.vo.WechatPaymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.order.dao.PaymentInfoDao;
import com.mall.sales.order.entity.PaymentInfoEntity;
import com.mall.sales.order.service.PaymentInfoService;

@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PaymentInfoEntity> page = this.page(
                new Query<PaymentInfoEntity>().getPage(params),
                new QueryWrapper<PaymentInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 使用支付宝网页扫码方式
     */
    @Override
    public AliPaymentVO getOrderToAliPayment(String orderSn) {
        //保存订单信息
        OrderEntity orderEntity = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
        if(null != orderEntity) {
            AliPaymentVO payVo = new AliPaymentVO();
            payVo.setOut_trade_no(orderSn);
            //必须只保留两位小数，否则支付宝无法识别
            BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, RoundingMode.UP);
            payVo.setTotal_amount(payAmount.toString());
            //TODO 临时处理
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
            if(null!=orderItemEntities) {
                OrderItemEntity orderItemEntity = orderItemEntities.get(0);
                payVo.setSubject(orderItemEntity.getSkuName());
                payVo.setBody(orderItemEntity.getSkuAttrsVals());
            }
            return payVo;
        }
        log.warn("没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        return null;
    }

    /**
     * 使用微信二维码扫码方式
     */
    @Override
    public WechatPaymentVO getOrderToWechatPayment(String orderSn) {
        //保存订单信息
        OrderEntity orderEntity = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
        if(null != orderEntity) {
            WechatPaymentVO payVo = new WechatPaymentVO();
            payVo.setOut_trade_no(orderSn);
            //保留两位小数
            BigDecimal totalPrice = orderEntity.getPayAmount().setScale(2, RoundingMode.UP);
            payVo.getAmout().setTotal(totalPrice);
            //TODO 简单处理
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
            if(null!=orderItemEntities) {
                OrderItemEntity orderItemEntity = orderItemEntities.get(0);
                payVo.setBody(orderItemEntity.getSkuAttrsVals());
            }
            return payVo;
        }
        log.warn("没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        return null;
    }

}