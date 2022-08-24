package com.mall.common.constant;

/**
 * 支付宝支付Native类型
 * https://opendocs.alipay.com/open/028r8t?ref=api&scene=22
 */
public class NativeOfAliPaymentConstant {

    /**
     * 支付结果通知
     */
    public enum tradeState{
        WAIT_BUYER_PAY(0,"WAIT_BUYER_PAY"),
        TRADE_SUCCESS(1,"TRADE_SUCCESS"),
        ORDER_CLOSED(2,"ORDER_CLOSED"),
        TRADE_FINISHED(3,"TRADE_FINISHED");

        private int code;
        private String type;

        tradeState(int code,String type){
            this.code = code;
            this.type = type;
        }
        public int getCode() {
            return code;
        }
        public String getType() {
            return type;
        }
    }

    /**
     * 支付结果通知
     */
    public enum refundState{
        REFUND_SUCCESS (0,"REFUND_SUCCESS ");

        private int code;
        private String type;

        refundState(int code,String type){
            this.code = code;
            this.type = type;
        }
        public int getCode() {
            return code;
        }
        public String getType() {
            return type;
        }
    }
}
