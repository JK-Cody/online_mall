package com.mall.sales.order.vo;

import lombok.Data;

import java.util.HashMap;

/**
 * 微信退款申请参数
 */
@Data
public class WechatPaymentRefundApplyVO {

    private String out_trade_no;  //商户订单号
    private String out_refund_no;  //商户退款单号
    private String reason;  //退款理由
    private String funds_account;  //退款资金来源,默认使用未结算资金退款
    private Amount amount;

    @Data
    public static class Amount{

        private String total;  //原订单支付金额
        private String refund;  //实际支付金额
        private String currency;
    }

}


