package com.mall.common.constant;

/**
 * 微信支付Native类型
 * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml
 */
public class NativeOfWechatPaymentConstant {

    public static final String CODE_URL = "wechatpayment:codeurl:";

    /**
     * 订单操作
     */
    public enum requestURL{
        CREATE_PAYMENT_ORDER("Native下单","/v3/pay/transactions/native"),
        GET_PAYMENT_BY_TRANSACTION_ID("通过transaction_id查询订单","/v3/pay/transactions/id/%s"),
        GET_PAYMENT_BY_OUT_TRADE_NO("通过out_trade_no查询订单","/v3/pay/transactions/out-trade-no/%s"),
        CLOSE_PAYMENT_ORDER("关闭订单","/v3/pay/transactions/out-trade-no/%s/close"),
        APPLY_PAYMENT_REFUND("申请单笔退款","/v3/refund/domestic/refunds"),
        GET_PAYMENT_REFUND_INFO("查询单笔退款","/v3/refund/domestic/refunds/%s"), //添加退款单号out_refund_no
        GET_TRADE_BILL("申请交易账单","/v3/bill/tradebill"),
        GET_FUNDFLOW_BILL("申请资金账单","/v3/bill/fundflowbill");

        private String name;
        private String url;

        requestURL(String name, String url){
            this.name = name;
            this.url = url;
        }
        public String getName() {
            return name;
        }
        public String getUrl() {
            return url;
        }
    }

    /**
     * 请求响应状态码
     */
    public enum httpCode{
        SUCCESS(200 ,"处理成功"),
        ACCEPTED(202 ,"服务器已接受请求，但尚未处理"),
        NO_CONTENT(204 ,"处理成功，无返回Body"),
        BAD_REQUEST(400 ,"协议或者参数非法"),
        UNAUTHORIZED(401,"签名验证失败"),
        FORBIDDEN(403,"权限异常"),
        NOT_FOUND(404 ,"请求的资源不存在");

        private int code;
        private String message;

        httpCode(int code,String message){
            this.code = code;
            this.message = message;
        }
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return message;
        }
    }

    /**
     * 支付结果通知
     */
    public enum tradeState{
        NOTPAY(0,"NOTPAY"),  //未支付
        SUCCESS(1,"SUCCESS"), //支付成功
        CLOSED(2,"CLOSED"),  //支付已关闭
        REVOKED(3,"REVOKED"), //已撤销（付款码支付）
        REFUND(4,"REFUND"),  //退款中
        USERPAYING(5,"USERPAYING"),//用户支付中（付款码支付）
        PAYERROR(6,"PAYERROR");  //支付失败(其他原因，如银行返回失败)

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


}
