package com.mall.sales.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.gson.Gson;
import com.mall.common.constant.NativeOfWechatPaymentConstant;
import com.mall.common.constant.OrderConstant;
import com.mall.common.exception.StockException;
import com.mall.common.utils.AesUtil;
import com.mall.common.utils.HttpUtils;
import com.mall.sales.order.config.AliPaymentTemplate;
import com.mall.sales.order.config.WechatPaymentTemplate;
import com.mall.sales.order.vo.*;
import com.mall.sales.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * 从order模块页面跳转到其他模块
 */
@Slf4j
@Controller
public class JumpPageController {

    @Autowired
    OrderService orderService;

    @Autowired
    AliPaymentTemplate aliPaymentTemplate;

    @Autowired
    WechatPaymentTemplate wechatPaymentTemplate;

    /**
     * 订单确认信息展示
     */
    @GetMapping("/cartItemToTrade")
    public String getCartItemToTrade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVO confirmVo = orderService.saveCreatedOrder();
        model.addAttribute("orderConfirmVO", confirmVo);
        return "confirm";  //返回页面
    }

    /**
     * 订单提交信息展示
     */
    @PostMapping("/cartItemToSubmit")
    public String getCartItemToSubmit(OrderMarkInfoOfSubmittedDataVO orderMarkInfoOfSubmittedDataVO, Model model, RedirectAttributes attributes) {
//提交订单
        try {
            ResponseOfCreatingOrderVO responseOfCreatingOrderVO = orderService.createOrderWithSubmittedData(orderMarkInfoOfSubmittedDataVO);
            Integer code = responseOfCreatingOrderVO.getCode();
            if(code == OrderConstant.AttrEnum.SUBMIT_ORDER_SUCCEED.getCode()){
                model.addAttribute("responseOfCreatingOrderVO", responseOfCreatingOrderVO);
                return "pay";  //返回页面
            }else{
                //保存错误信息
                String msg="";
                switch (code){
                    case 1: msg = OrderConstant.AttrEnum.CHECK_ORDER_TOKEN_FAILURE.getMsg() ;
                    break;
                    case 2: msg = OrderConstant.AttrEnum.CHECK_ORDER_PAYAMOUNT_FAILURE.getMsg();
                    break;
                }
                attributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.mall.com/cartItemToTrade";
            }
        } catch (Exception e) {
            //保存库存异常的错误信息
            if (e instanceof StockException) {
                log.error("保存订单时发生异常错误",e);
                attributes.addFlashAttribute("msg",e.getMessage());
            }
            return "redirect:http://order.mall.com/cartItemToTrade";
        }
    }

    /**
     * 处理支付宝支付的结果,回调给notify_url
     */
    @ResponseBody
    @PostMapping("/AfterAliPayment/returnPaymentNotification")
    public String handleNotificationAfterAliPayment(AliPaymentNotificationVO vo, HttpServletRequest request) throws AlipayApiException {
        //获取支付宝支付的结果
        Boolean result = aliPaymentTemplate.handlePaymentNotification(request);
        if (result) {
            //校验通知数据的商家账号+应用的id的正确性
            Boolean verifyResult = aliPaymentTemplate.verifyNotificationData(vo);
            if(verifyResult){
                //保存订单支付结果
                return orderService.handleAliPaymentNotification(vo);
            }else {
                return "error";
            }
        } else {
            return "error";
        }
    }

    /**
     * 处理微信支付的结果通知,回调给 notify_url
     */
    @ResponseBody
    @PostMapping("/AfterWechatPayment/returnPaymentNotification")
    public String handleNotificationAfterWechatPayment(HttpServletRequest request, HttpServletResponse response) {
        //获取微信支付的结果
        Map<String,String> responseMap=new HashMap();
        HashMap encryptCertificateMap = wechatPaymentTemplate.handlePaymentNotification(request);
        if(null!=encryptCertificateMap){
            try {
                String trade_state = (String) encryptCertificateMap.get("trade_state");
                if (Objects.equals(trade_state, NativeOfWechatPaymentConstant.tradeState.SUCCESS.getType())) {
                    //保存订单支付结果
                    String out_trade_no = (String) encryptCertificateMap.get("out_trade_no");
                    orderService.handleWechaPaymentNotification(encryptCertificateMap, out_trade_no);
                    //返回响应状态
                    response.setStatus(NativeOfWechatPaymentConstant.httpCode.SUCCESS.getCode());
                    responseMap.put("code", "SUCCESS");
                    responseMap.put("message", NativeOfWechatPaymentConstant.httpCode.SUCCESS.getMessage());
                    return new Gson().toJson(responseMap);
                }
            }catch (Exception e) {
                //返回响应状态
                response.setStatus(500);
                responseMap.put("code","FAIL");
                responseMap.put("message","FAIL");
                return new Gson().toJson(responseMap);
            }
        }
        //返回响应状态
        response.setStatus(NativeOfWechatPaymentConstant.httpCode.UNAUTHORIZED.getCode());
        responseMap.put("code","FAIL");
        responseMap.put("message",NativeOfWechatPaymentConstant.httpCode.UNAUTHORIZED.getMessage());
        return new Gson().toJson(responseMap);
    }

}
