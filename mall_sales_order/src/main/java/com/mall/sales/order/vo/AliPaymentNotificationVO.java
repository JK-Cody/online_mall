package com.mall.sales.order.vo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 支付宝支付的结果通知所携带的参数
 */
@ToString
@Data
@Slf4j
public class AliPaymentNotificationVO {

    private String gmt_create;
    private String charset;
    private String gmt_payment;
    private String notify_time;
    private String subject;
    private String sign;
    private String buyer_id; //支付者的id
    private String body;//订单的信息
    private String invoice_amount;//支付金额
    private String version;
    private String notify_id;//通知id
    private String fund_bill_list;
    private String notify_type;//通知类型
    private String out_trade_no;//订单号
    private String total_amount;//支付的总额
    private String trade_status;//交易状态
    private String trade_no;//流水号
    private String auth_app_id;
    private String receipt_amount;//商家收到的退款
    private String point_amount;
    private String app_id;//应用id
    private String buyer_pay_amount;//最终支付的金额
    private String sign_type;//签名类型
    private String seller_id;//商家的id

}
