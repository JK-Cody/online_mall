package com.mall.sales.order.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.mall.common.utils.HttpUtils;
import com.mall.sales.order.vo.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付宝Native支付的沙箱测试应用配置
 * https://opendocs.alipay.com/open/02ivbu
 */
@ConfigurationProperties(prefix = "alipayment")  //配置文件的参数前缀(非必须)
@Component
@Data
@Slf4j
public class AliPaymentTemplate {
    // 支付宝网关
    private final String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    //在支付宝创建的应用的id
    private final String app_id = "2021000120608424";
//    绑定的商家账号（PID）
    private final String seller_id = "2088621959318811";
    // 商户私钥
    private final String merchant_private_key ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCsWllQOi0b/lGJsyeIeYdIudvoA7zmNbtd+5ntu0YpAYVSX5AO/LXTkQg+CkZwWIYqLr4xD/IaMfcoNoDcFjjXl0WocRQNtJuoa6HpwoVCOkzSP1qqlAWJxbJckJ0qC3Kd1//vuFQnwaTu5X/yiADqKuZs02KEwWiTcJ9R0CA9q+tuhL2imqfq5nIr8vS88cRCO02pOBrlFQMSsUvF3B4GZeZW4CHKvGF1Vt6oFRj7adkhfOpDEM1mBlWZ5PfXNzR2XXdK7JdymzGulf3S1dBK6Nr8CRK3eJFcicJV+FNBryU+ZfIht+ZOFPFHVRTvBWYN3wBlLphAogtwZwsDY2nlAgMBAAECggEAGOGOJTPQ0Uq8kKlpWp1Yt1oFchAgFAfLvbyVsykm7sKGcabUECXRRfKZAwPOuWc+LxbCZCjOJZPy/z4nCh63JAB1kOSKJZReKEkpfaqUPERYVL3Ko/dseUAuCIjj927hpAHLs9qjY33kBozhFK0nnKaGz00Q2uNk6mwGw/PMw7oyQXMLjbk7dAsr2caL/o8CDUoKUUv5q5d2XcCqbbq/RAsCYU0D4Ap0BaMAJkccGniwYXxF1eZX5NQejS81FwxvoMcFOuMyC/Do6hWvRMnLz0r8/AIeYIyVJwBD1VU4Ut6l8fwNzw/wO940Vh9NDYloCPnm+PIES7h4V9+OWoZX4QKBgQDVuHiM3g6rkrRram5OxxP8K23MI4D5qW0wCbXgbXvAwS0HQazu31KGu6O7IAdGITxNbDSb5nDWA3U+58NxoKfIB+nw8PaxGt1a4wKRyzOodvHugh83Sfe5kdu9ONnmNZlxQWuIZdt5eUzPbpsk6fnBk8A3T5fc0EqwcES5Fmp53QKBgQDOcuFiSn3wBoQf5Qj/VOVCoPXxIRqm1RLJnOjoDANBn1HA9Vds54IY2WJ0zrlBc5PwXGC0Ql+SpEqXUs+Ar6/LUdU7kHjIhUg4SXZuzdW2ekxd58SdQaSDmNqW+Dya/XJpC9VSTE5OXly0YuWGJPZHJF+sTFr7CIY4DwvGMZLjqQKBgCi3uo/sBpN9oc4NOvLbTt1uUL7hzMLEGr9aOkKF2VwB8dg+FPp1pb1xpmISWhBsaJa/lM5W+wxrID1Z3oyZdR3RFCsJXZakbaPROD4anr7r2m/8boIUH5RFS74jRLBtacyAEEKuVUAxcgbbJcfEpAbTZX2kNmSlcnVnDlDNCoApAoGAFT6NIug3fXOuNPoEqwG5ddnKdT3K2FAk1spmzTovO5mBeDHHBVXtdVEpaIu9VJboKlvXN7V80EJHyrqiz8NEe8+3+aEsTLYqcGBe55gW3ehH44HuIaCXpVMda1uwIOLj2xt0oDtp83aSY52psNws27VKgxh4ZEzEQxCJTaPcUcECgYAqqep6jvyzcztR6GG49FGK3l4nHCJzR4k1HG6LEyOpnLzWGX+IwpEkZ2v7fLH4XXVdu9nXyo//CYv81KBd1+jy0GaZ9DMT8K7TWO/GWlPDWeoCZVAWFXpLzwbvno0csHWB77BwME0HGNc9AVS/yr6NwPnvv4Hkzkfvcjd1QgxNSg==";
    // 支付宝公钥
    private final String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr7UuieaCJztz+CcYKQMjzL3m7F3k5qAtybadroUSB2ViIO3bQzIygG3hx5jqS3oXusF+NWsSxwQieUm1/Z6iNFsLQDsR2cG+BAmuRyAZ7TVgzG73lbLR9Ldt62no0mAA1QDqhjBJjN6f0dquT2cQt25U/kSkIdWKdaxlM96BVS6wZKFrbP8V7d2JeLQJmMnKM1kuZk+nM7hN7Sr1f6/p1MZiNKVps5qdxS/8sQbQbLGqWCQqtbbGj8RBCQ3+Z8RICR/l0YfmZuhfcDEprZC+m1NgTaaSdJ0/UnDGfELgSmYVR+1sNJBEvN7E/l2VRQ9KEsFYfJOV3N6Gjjc0kXL2IQIDAQAB";
    //支付宝在支付成功时的回调给支付人的网址
    /* 返回支付结果的信息，必须外网能够访问,不能加网页自定义参数 */
    private final String notify_url = "http://38jt9z.natappfree.cc/AfterAliPayment/returnPaymentNotification"; //natappfree内网穿透
    //支付成功时的回调地址(返回商城的页面),不能加网页自定义参数
    private final String return_url = "http://member.mall.com/memberOrder.html";
    // 签名加密方式
    private final String sign_type = "RSA2";
    // 字符编码格式
    private final String charset = "utf-8";
    // 支付宝预定的支付超时时间
    private final String timeout_express = "1m";

    /**
     * 发送支付宝付款请求
     */
    public String toPayment(AliPaymentVO aliPaymentVO)  {
//根据请求参数生成支付客户端
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
        //创建支付请求
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(return_url);
            alipayRequest.setNotifyUrl(notify_url);
            //支付宝交易流水号
            String payment_trade_no = aliPaymentVO.getOut_trade_no(); //必填
            //付款总额
            String total_amount = aliPaymentVO.getTotal_amount(); //必填
            //订单名称
            String subject = aliPaymentVO.getSubject(); //必填
            //自写备注信息
            String body = aliPaymentVO.getBody(); //可空
            alipayRequest.setBizContent("{\"out_trade_no\":\"" + payment_trade_no + "\","
                    + "\"total_amount\":\"" + total_amount + "\","
                    + "\"subject\":\"" + subject + "\","
                    + "\"body\":\"" + body + "\","
                    + "\"timeout_express\":\"" +timeout_express+ "\","   //设置支付等待时间
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
//执行请求
            return alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException("failure-发送支付宝付款请求",e);
        }
    }

    /**
     * 获取支付宝支付的结果
     */
    public Boolean handlePaymentNotification(HttpServletRequest request)throws AlipayApiException {
        //读取支付宝Native支付返回的支付信息
        Map<String, String> returnInfo = HttpUtils.readRequestIntoMap(request);
        //验签
        boolean signVerified = AlipaySignature.rsaCheckV1(returnInfo, this.getAlipay_public_key(), this.getCharset(), this.getSign_type());
        if (signVerified) {
            log.info("支付宝签名验证成功");
            return true;
        } else {
            log.warn("支付宝签名验证失败");
            //返回给支付宝
            return false;
        }
    }

    /**
     * 校验通知数据的商家账号+应用的id的正确性
     * https://opendocs.alipay.com/open/203/105286
     */
    public Boolean verifyNotificationData(@RequestBody AliPaymentNotificationVO vo)throws AlipayApiException {

        if(vo.getSeller_id().equals(seller_id) && vo.getApp_id().equals(app_id)) {
            log.info("success-验证seller_id和app_id");
            return true;
        }else {
            log.warn("failure-验证seller_id和app_id");
            return false;
        }
    }

    /**
     * 向支付宝申请交易关闭
     */
    public Boolean closePaymentTrade(@RequestParam("outTradeNo")  String out_trade_no)throws AlipayApiException {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no",out_trade_no);
            request.setBizContent(bizContent.toString());
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("success-向支付宝申请交易关闭"+"body="+response.getBody());
                return true;
            } else {
                log.warn("failure-向支付宝申请交易关闭"+";"+"code="+response.getCode()+";"+"message="+response.getMsg()+";"+"body="+response.getBody());
                return false;
            }
        } catch (AlipayApiException e) {
           throw new RuntimeException("failure-向支付宝申请交易关闭",e);
        }
    }

    /**
     * 向支付宝查询支付订单
     */
    public String queryPaymentResult(@RequestParam("outTradeNo") String out_trade_no){

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", out_trade_no);
            request.setBizContent(bizContent.toString());
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String body = response.getBody();
                log.info("success-向支付宝查询支付订单"+"body="+body);
                JSONObject jsonObject = JSON.parseObject(body);
                return jsonObject.getString("trade_status");
            } else {
                log.warn("failure-向支付宝查询支付订单"+";"+"code="+response.getCode()+";"+"message="+response.getMsg()+";"+"body="+response.getBody());
                return null;
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("failure-向支付宝查询支付订单",e);
        }
    }

    /**
     * 向支付宝平台申请退款
     */
    public Boolean paymentRefundApply(@RequestBody AliPaymentRefundApplyVO vo) {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest ();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", vo.getOut_trade_no());
            bizContent.put("refund_amount", vo.getRefund_amount());
            bizContent.put("refund_reason", vo.getRefund_reason());
            //返回参数选项，按需传入
            /* 如需部分退款，则out_request_no必传 */
//            if(null!=vo.getOut_request_no()){
//                 bizContent.put("out_request_no", vo.getOut_request_no());
//            }
//            JSONArray queryOptions = new JSONArray();
//            queryOptions.add(vo.getRefund_royalty_parameters());
//            bizContent.put("query_options", queryOptions);
            request.setBizContent(bizContent.toString());
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String body = response.getBody();
                log.info("success-向支付宝平台申请退款"+"body="+body);
                return true;
            } else {
                log.warn("failure-向支付宝平台申请退款"+";"+"code="+response.getCode()+";"+"message="+response.getMsg()+";"+"body="+response.getBody());
                return false;
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("failure-向支付宝平台申请退款",e);
        }
    }

    /**
     * 向支付宝查询退款结果
     */
    public String getPaymentRefundResult(@RequestParam("outTradeNo") String out_trade_no,@RequestParam("cc") String out_request_no) {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest ();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", out_trade_no);
            /* 退款时使用out_request_no，则添加,否则用订单号代替 */
            if(null!=out_request_no && out_request_no.length()>0) {
                bizContent.put("out_request_no", out_request_no);
            }
            bizContent.put("out_request_no", out_trade_no);
            request.setBizContent(bizContent.toString());
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String body = response.getBody();
                log.info("success-向支付宝查询退款结果"+"body="+body);
                JSONObject jsonObject = JSON.parseObject(body);
                return jsonObject.getString("refund_status");
            } else {
                log.warn("failure-向支付宝查询退款结果"+";"+"code="+response.getCode()+";"+"message="+response.getMsg()+";"+"body="+response.getBody());
                return null;
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("failure-向支付宝查询退款结果",e);
        }
    }

    /**
     * 向支付宝申请账单
     * 返回下载网址
     */
    public String applyAliPaymentTradebill(@RequestBody AliPaymentTradebillApply vo) {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                    app_id, merchant_private_key, "json",
                    charset, alipay_public_key, sign_type);
            AlipayDataDataserviceBillDownloadurlQueryRequest  request = new AlipayDataDataserviceBillDownloadurlQueryRequest  ();
            JSONObject bizContent = new JSONObject();
            bizContent.put("bill_type", vo.getBill_type());
            bizContent.put("bill_date", vo.getBill_date());
            request.setBizContent(bizContent.toString());
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String body = response.getBody();
                log.info("success-向支付宝申请账单"+"body="+body);
                JSONObject jsonObject = JSON.parseObject(body);
                return jsonObject.getString("bill_download_url");
            } else {
                log.warn("failure-向支付宝申请账单"+";"+"code="+response.getCode()+";"+"message="+response.getMsg()+";"+"body="+response.getBody());
                return null;
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("failure-向支付宝申请账单",e);
        }
    }


}
