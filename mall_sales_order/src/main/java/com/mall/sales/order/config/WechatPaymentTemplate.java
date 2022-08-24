package com.mall.sales.order.config;

import com.google.gson.Gson;
import com.mall.common.constant.NativeOfWechatPaymentConstant;
import com.mall.common.utils.AesUtil;
import com.mall.common.utils.HttpUtils;
import com.mall.sales.order.vo.WechatPaymentRefundApplyVO;
import com.mall.sales.order.vo.WechatPaymentOfTradebillOrFundflowbillApplyVO;
import com.mall.sales.order.vo.WechatPaymentVO;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信Native支付的沙箱测试应用配置
 * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_1.shtml
 */
@ConfigurationProperties(prefix = "wechatpayment")  //配置文件的参数前缀(非必须)
@Component
@Data
@Slf4j
public class WechatPaymentTemplate {
    // 微信服务器
    private final String wechatDomainUrl = "https://api.mch.weixin.qq.com";
    // 商户私钥
    private final String privateKey = "";
    // 商户号
    private final String merchantId = "";
    // 公众号
    private final String appId = "";
    // 商户证书序列号
    private final String merchantSerialNumber = "";
    // apiV3密钥
    private final String apiV3Key = "";
    //微信支付在支付成功时的回调给支付人的网址
    /* 此时返回支付的所有信息，必须外网能够访问 */
    private final String notify_url="http://38jt9z.natappfree.cc/AfterWechatPayment/returnPaymentNotification"; //natappfree内网穿透
    //支付成功时的回调地址(返回商城的页面)
    private final String return_url = "http://member.mall.com/memberOrder.html";
    // 验签器
    private Verifier verifier;
    // 平台证书管理器
    private CertificatesManager certificatesManager;
    // 字符编码格式
    private final String charset = "utf-8";

    @Autowired
    CloseableHttpClient builderCertificate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private static AesUtil aesUtilForWechatPayment;

    /**
     * 发送支付请求,获取支付二维码
     */
    public Map<String,Object> toPayment(WechatPaymentVO wechatPaymentVO){

        Map<String,Object> resultMap=new HashMap<>();
//判断是否需要生成新支付二维码
        String redisCodeUrl = stringRedisTemplate.opsForValue().get(NativeOfWechatPaymentConstant.CODE_URL + wechatPaymentVO.getOut_trade_no());
        //已有缓存时
        if(null != redisCodeUrl && redisCodeUrl.length() > 0) {
            //保存页面createwechatQRcode.html需要的内容
            resultMap.put("codeUrl",redisCodeUrl);
            resultMap.put("orderSn",wechatPaymentVO.getOut_trade_no());
            resultMap.put("returnUrl",this.return_url);
            return resultMap;
        }else{
            //保存请求参数
            Map<String,Object> paymentInfo = new HashMap<>();
            paymentInfo.put("out_trade_no",wechatPaymentVO.getOut_trade_no());
            paymentInfo.put("description",wechatPaymentVO.getBody());
            paymentInfo.put("amount",wechatPaymentVO.getAmout());
            paymentInfo.put("appid",appId);
            paymentInfo.put("mchid",merchantId);
            paymentInfo.put("notify_url",notify_url);
//设置请求参数格式
            //转换
            Gson gson = new Gson();
            String submitPaymentInfo = gson.toJson(paymentInfo);
            StringEntity url = new StringEntity(String.valueOf(submitPaymentInfo),charset);
            url.setContentType("application/json");
            HttpPost httpPost = new HttpPost(wechatDomainUrl+NativeOfWechatPaymentConstant.requestURL.CREATE_PAYMENT_ORDER.getUrl());
            httpPost.setEntity(url);
            //设置请求头
            httpPost.setHeader("Accept", "application/json");
//完成签名并执行请求,并验证平台证书(自动)
            CloseableHttpResponse response=null;
            try {
                response = builderCertificate.execute(httpPost);
                String responseString = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                //处理成功
                if (statusCode == NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode()) {
                    log.info("success,return body = " +responseString);
                    //处理成功，无返回Body
                } else if (statusCode == NativeOfWechatPaymentConstant.httpCode.NO_CONTENT.getCode()) {
                    log.info("success");
                } else {
                    log.info("failed,resp_code = " + statusCode+ ",return_body = " + responseString);
                    throw new IOException("request failed");
                }
                Map<String,String> responseStringMap = gson.fromJson(responseString, HashMap.class);
                String codeUrl = responseStringMap.get("code_url");
//将支付二维码保存在redis,二维码有效时间为两小时
                stringRedisTemplate.opsForValue().set
                        (NativeOfWechatPaymentConstant.CODE_URL+wechatPaymentVO.getOut_trade_no(), codeUrl, 2, TimeUnit.HOURS);
//保存页面createwechatQRcode.html需要的内容
                resultMap.put("codeUrl",codeUrl);
                resultMap.put("orderSn",wechatPaymentVO.getOut_trade_no());
                resultMap.put("returnUrl",this.return_url);
            }catch (IOException | org.apache.http.ParseException e) {
                throw new RuntimeException("failure-向微信平台查询订单是否已支付",e);
            }finally {
                try {
                    if(null!=response) {
                        response.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return resultMap;
        }
    }

    /**
     * 处理支付宝支付的结果,验签与解密
     */
    public HashMap handlePaymentNotification(HttpServletRequest request) {
        //获取微信Native支付的返回支付信息,转为String
        String body = HttpUtils.readRequestIntoString(request); //作为应答主体
        //回调通知的验签与解密
        HashMap encryptCertificateMap = this.getEncryptCertificate(request, body);
        //因为上一步已集成解密，所以不需要手动解密
//        Gson gson = new Gson();
//        Map<String, Object> returnInfoMap = gson.fromJson(body, HashMap.class);
//        String returnInfo = wechatPaymentTemplate.checkPaymentNotification(returnInfoMap);
        if(null!=encryptCertificateMap){
            log.info("success-微信支付结果的验签与解密");
            return encryptCertificateMap;
        }else{
            log.warn("failure-微信支付结果的验签与解密");
            return null;
        }
    }

    /**
     * 通过out_trade_no(数据库payment_trade_no)向微信平台查询订单是否已支付
     * 长时间未收到支付成功通知时，手动调用
     */
    public String queryPaymentResult(@RequestParam("orderSn") String orderSn) {
//设置请求地址参数
        String queryUrl = String.format(NativeOfWechatPaymentConstant.requestURL.GET_PAYMENT_BY_OUT_TRADE_NO.getUrl(), orderSn);
        queryUrl = wechatDomainUrl.concat(queryUrl).concat("?mchid").concat(merchantId);
        //设置请求头
        HttpGet httpGet = new HttpGet(queryUrl);
        httpGet.setHeader("Accept", "application/json");
//完成签名并执行请求,并验证平台证书(自动)
        CloseableHttpResponse response=null;
        try {
            //完成签名并执行请求,并验证平台证书(自动)
            response = builderCertificate.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            Map hashMap = new Gson().fromJson(responseString, HashMap.class);
            String trade_state = (String) hashMap.get("trade_state");
            //处理成功
            if (statusCode == NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode()) {
                log.info("success,return body = " + responseString);
                //返回状态
                return trade_state;
                //处理成功，无返回Body
            } else if (statusCode == NativeOfWechatPaymentConstant.httpCode.NO_CONTENT.getCode()) {
                log.info("success");
                //返回状态
                return trade_state;
            } else {
                log.info("failed,resp_code = " + statusCode + ",return_body = " + responseString);
                throw new IOException("request failed");
            }
        } catch (IOException | org.apache.http.ParseException e) {
            throw new RuntimeException("failure-向微信平台查询订单是否已支付",e);
        }finally {
            try {
                if(null!=response) {
                    response.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 回调通知的解密(手动)
     */
    public String checkPaymentNotification(Map<String, Object> bodyMap) {

        Map<String,String> resourceMap = (Map) bodyMap.get("resource");
        String associated_data = resourceMap.get("associated_data");
        String ciphertext = resourceMap.get("ciphertext");
        String nonce = resourceMap.get("nonce");
        try {
            aesUtilForWechatPayment = new AesUtil(apiV3Key.getBytes(charset));
            try {
                String certResult = aesUtilForWechatPayment.decryptToString(associated_data.getBytes(charset),
                        nonce.getBytes(charset),
                        ciphertext);
                return certResult;
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException("转换为字符串错误",e);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("解密平台证书错误",e);
        }
    }

    /**
     * 回调通知的验签与解密
     */
    public HashMap getEncryptCertificate(HttpServletRequest request,String body) {
//获取请求返回的验证平台证书的参数
        String signature = request.getHeader("Wechatpay-Signature"); //应答签名串
        String timestamp = request.getHeader("Wechatpay-Timestamp"); //应答时间戳
        String nonce = request.getHeader("Wechatpay-Nonce");    //答随机串
        String serial = request.getHeader("Wechatpay-Serial");  // 平台证书序列号
//        String body = JSONObject.toJSONString(request);  //应答主体
//验证(回调通知的验签)
        NotificationRequest notificationRequest = new NotificationRequest.Builder().withSerialNumber(serial)
                .withNonce(nonce)
                .withTimestamp(timestamp)
                .withSignature(signature)
                .withBody(body)
                .build();
        NotificationHandler handler = null;
        try {
            handler = new NotificationHandler(verifier, apiV3Key.getBytes(charset));
//解密回调通知
            Notification notification = handler.parse(notificationRequest);
            if(null!=notification){
                log.info("success-解密回调通知");
                //获取解密报文
                String decryptData = notification.getDecryptData();
                //转换
                HashMap encryptCertificateMap = new Gson().fromJson(decryptData, HashMap.class);
                return encryptCertificateMap;
            }else {
                log.warn("failure-解密回调通知");
                return null;
            }
        } catch (UnsupportedEncodingException | ValidationException | ParseException | ClassCastException e) {
            throw new RuntimeException("异常-回调通知的验签与解密",e);
        }
    }

    /**
     * 创建平台证书的验证请求
     * 定时更新平台证书,相当于每次调用的初始化
     */
    @Bean //调用一次
    public CloseableHttpClient builderCertificate(){
//加载平台证书(公钥)
        PrivateKey merchantPrivateKey = this.loadPrivateKey();
//更新平台证书(公钥)
        try {
            // 获取证书管理器实例
            certificatesManager = CertificatesManager.getInstance();
            // 向证书管理器增加需要自动更新平台证书的商户信息
            //若有多个商户号，可继续调用putMerchant添加商户信息
            certificatesManager.putMerchant(merchantId,
                    //验证私钥签名对象
                    new WechatPay2Credentials(merchantId, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)), //私钥签名对象
                    //APIV3加密
                    apiV3Key.getBytes(charset));
            // 从证书管理器中获取verifier
            verifier = certificatesManager.getVerifier(merchantId);
//创建请求
            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(verifier));
            return builder.build();
        } catch (IOException | GeneralSecurityException | HttpCodeException | NotFoundException e) {
            throw new RuntimeException("更新平台证书错误",e);
        }
    }

    /**
     * 加载平台证书
     */
    private PrivateKey loadPrivateKey(){

        try {
            //方式一：私钥为String字符串
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                    new ByteArrayInputStream(this.privateKey.getBytes(charset)));
            //方式二: 私钥存储在文件
//            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
//                    new FileInputStream("/path/to/apiclient_key.pem"));
            return merchantPrivateKey;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("商户私钥未找到",e);
        }
    }

    /**
     * 向微信平台申请退款
     */
    public Map paymentRefundApply(@RequestBody WechatPaymentRefundApplyVO vo) {
//设置请求参数格式
        String url = wechatDomainUrl.concat(NativeOfWechatPaymentConstant.requestURL.APPLY_PAYMENT_REFUND.getUrl());
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",vo.getOut_trade_no());
        map.put("out_refund_no",vo.getOut_refund_no());
        map.put("reason",vo.getReason());
        map.put("notify_url",this.return_url);
        map.put("funds_account",vo.getFunds_account());
        map.put("amout",vo.getAmount());
        //转换
        Gson gson = new Gson();
        String str = gson.toJson(map);
        StringEntity entity = new StringEntity(str,charset);
        entity.setContentType("application/json");
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        //设置请求头
        httpPost.setHeader("Accept", "application/json");
//完成签名并执行请求,并验证平台证书(自动)
        CloseableHttpResponse response=null;
        try {
            response = builderCertificate.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            Map hashMap = gson.fromJson(responseString, HashMap.class);
            //处理成功
            if (statusCode == NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode()) {
                log.info("success,return body = " +responseString);
                return hashMap;
                //处理成功，无返回Body
            } else if (statusCode == NativeOfWechatPaymentConstant.httpCode.NO_CONTENT.getCode()) {
                log.info("success");
                return hashMap;
            } else {
                log.info("failed,resp_code = " + statusCode+ ",return_body = " + responseString);
                throw new IOException("request failed");
            }
        }catch (IOException | org.apache.http.ParseException e) {
            throw new RuntimeException("failure-向微信平台申请退款",e);
        }finally {
            try {
                if(null!=response) {
                    response.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 向微信平台查询退款结果
     */
    public Map getPaymentRefundResult(@RequestParam("outRefundNo") String out_refund_no) {
//设置请求参数格式
        String queryUrl = String.format(NativeOfWechatPaymentConstant.requestURL.GET_PAYMENT_REFUND_INFO.getUrl(), out_refund_no);
        queryUrl = wechatDomainUrl.concat(queryUrl);
        HttpPost httpPost = new HttpPost(queryUrl);
        //设置请求头
        httpPost.setHeader("Accept", "application/json");
//完成签名并执行请求,并验证平台证书(自动)
        CloseableHttpResponse response=null;
        try {
            response = builderCertificate.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            Map hashMap =  new Gson().fromJson(responseString, HashMap.class);
            //处理成功
            if (statusCode == NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode()) {
                log.info("success,return body = " +responseString);
                return hashMap;
                //处理成功，无返回Body
            } else if (statusCode == NativeOfWechatPaymentConstant.httpCode.NO_CONTENT.getCode()) {
                log.info("success");
                return hashMap;
            } else {
                log.info("failed,resp_code = " + statusCode+ ",return_body = " + responseString);
                throw new IOException("request failed");
            }
        }catch (IOException | org.apache.http.ParseException e) {
            throw new RuntimeException("failure-向微信平台查询退款结果",e);
        }finally {
            try {
                if(null!=response) {
                    response.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 申请交易账单 或 申请资金账单
     * 返回下载网址
     */
    public String applyPaymentOfTradebillOrFundflowbill(@RequestBody WechatPaymentOfTradebillOrFundflowbillApplyVO vo) {
//判断类型
        String queryUrl = "";
        if(vo.getType().equals(WechatPaymentOfTradebillOrFundflowbillApplyVO.bill_type)) {
//设置请求参数格式——申请交易账单
            queryUrl =NativeOfWechatPaymentConstant.requestURL.GET_TRADE_BILL.getUrl();
//设置请求参数格式——申请资金账单
        }else if(vo.getType().equals(WechatPaymentOfTradebillOrFundflowbillApplyVO.account_type)){
            queryUrl =NativeOfWechatPaymentConstant.requestURL.GET_FUNDFLOW_BILL.getUrl();
        }else{
            throw new RuntimeException("无此类账单可申请");
        }
        //必须有bill_data
        queryUrl = wechatDomainUrl.concat(queryUrl).concat("?bill_data=").concat(vo.getBill_date());
        HttpPost httpPost = new HttpPost(queryUrl);
        //设置请求头
        httpPost.setHeader("Accept", "application/json");
        CloseableHttpResponse response=null;
        try {
            response = builderCertificate.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            Map hashMap =  new Gson().fromJson(responseString, HashMap.class);
            //处理成功
            if (statusCode == NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode()) {
                log.info("success,return body = " +responseString);
                String download_url = (String) hashMap.get("download_url");
                return download_url;
                //处理成功，无返回Body
            } else if (statusCode == NativeOfWechatPaymentConstant.httpCode.NO_CONTENT.getCode()) {
                log.info("success");
                String download_url = (String) hashMap.get("download_url");
                return download_url;
            } else {
                log.info("failed,resp_code = " + statusCode+ ",return_body = " + responseString);
                throw new IOException("request failed");
            }
        }catch (IOException | org.apache.http.ParseException e) {
            throw new RuntimeException("failure-申请交易账单/申请资金账单",e);
        }finally {
            try {
                if(null!=response) {
                    response.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
