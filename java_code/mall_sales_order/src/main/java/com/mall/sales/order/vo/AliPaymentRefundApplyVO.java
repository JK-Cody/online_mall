package com.mall.sales.order.vo;

import lombok.Data;

/**
 * 支付宝支付申请退款
 */
@Data
public class AliPaymentRefundApplyVO {

    public static final String refund_detail_item_list="refund_detail_item_list";
    public static final String deposit_back_info="deposit_back_info";

    private String out_trade_no;  //商户订单号
    private String refund_amount;  //退款金额
    private String refund_reason;  //退款理由
    private String out_request_no;  //退款请求号
    private String refund_royalty_parameters;  //退分账明细信息,有refund_detail_item_list 或 deposit_back_info

}
