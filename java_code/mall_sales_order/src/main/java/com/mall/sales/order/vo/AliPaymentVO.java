package com.mall.sales.order.vo;

import lombok.Data;

/**
 * 支付宝支付单信息
 */
@Data
public class AliPaymentVO {

    private String out_trade_no;  //支付宝交易流水号,等同订单号orderSn
    private String total_amount;   //支付总金额
    private String subject;  //支付宝交易订单名称
    private String body;  //自写备注信息

}
