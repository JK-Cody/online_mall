package com.mall.sales.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付单信息
 */
@Data
public class WechatPaymentVO {

    private String out_trade_no;  //微信支付交易流水号,等同订单号orderSn
    private String body;  //自写备注信息
    private Amount amout;

    @Data
    public static class Amount{

        private BigDecimal total;  //支付总金额
        private String currencyType="CNY";   //支付的货币类型,默认人民币
    }
}
