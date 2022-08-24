package com.mall.sales.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.order.entity.PaymentInfoEntity;
import com.mall.sales.order.vo.AliPaymentVO;
import com.mall.sales.order.vo.WechatPaymentVO;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 00:39:26
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AliPaymentVO getOrderToAliPayment(String orderSn);

    WechatPaymentVO getOrderToWechatPayment(String orderSn);

}

