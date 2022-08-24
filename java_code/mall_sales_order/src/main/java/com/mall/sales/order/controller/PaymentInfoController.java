package com.mall.sales.order.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.common.constant.NativeOfWechatPaymentConstant;
import com.mall.sales.order.config.AliPaymentTemplate;
import com.mall.sales.order.config.WechatPaymentTemplate;
import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.service.OrderService;
import com.mall.sales.order.vo.AliPaymentVO;
import com.mall.sales.order.vo.WechatPaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.order.entity.PaymentInfoEntity;
import com.mall.sales.order.service.PaymentInfoService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 支付信息
 */
@Slf4j
@Controller
@RequestMapping("order/paymentinfo")
public class PaymentInfoController {

    @Autowired
    OrderService orderService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private AliPaymentTemplate aliPaymentTemplate;

    @Autowired
    private WechatPaymentTemplate wechatPaymentTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = paymentInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PaymentInfoEntity paymentInfo = paymentInfoService.getById(id);

        return R.ok().put("paymentInfo", paymentInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PaymentInfoEntity paymentInfo){
		paymentInfoService.save(paymentInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PaymentInfoEntity paymentInfo){
		paymentInfoService.updateById(paymentInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		paymentInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 使用支付宝二维码扫码方式
     */
    @ResponseBody
    @GetMapping(value = "/aliPaymentForOrder",produces = "text/html")  //更改响应的数据为文本格式
    public String getOrderToAliPayment(@RequestParam("orderSn") String orderSn,
                                       Model model ) throws AlipayApiException {

        AliPaymentVO aliPaymentVO = paymentInfoService.getOrderToAliPayment(orderSn);
        if(null!=aliPaymentVO) {
            return aliPaymentTemplate.toPayment(aliPaymentVO);
        }
        log.warn("getOrderToAliPayment():没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        //返回出错页面
        model.addAttribute("message","没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        return "errororder";
    }

    /**
     * 使用微信二维码扫码方式
     */
    @GetMapping(value = "/wechatPaymentForOrder")
    public String getOrderToWechatPayment(@RequestParam("orderSn") String orderSn,
                            Model model ) throws IOException {

        WechatPaymentVO wechatPaymentVO =paymentInfoService.getOrderToWechatPayment(orderSn);
        if(null!=wechatPaymentVO) {
            Map<String, Object> paymentMap = wechatPaymentTemplate.toPayment(wechatPaymentVO);
            //保存页面createwechatQRcode.html需要的内容
            model.addAttribute("codeUrl", paymentMap.get("codeUrl"));
            model.addAttribute("orderSn", paymentMap.get("orderSn"));
            model.addAttribute("returnUrl", paymentMap.get("returnUrl"));
            return "createwechatQRcode";
        }
        log.warn("getOrderToWechatPayment():没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        //返回出错页面
        model.addAttribute("message","没有查询到该订单:{"+orderSn+"}，无法创建付款码");
        return "errororder";
    }

    /**
     * 查询订单支付状态，返回到页面 createwechatQRcode.html
     */
    @ResponseBody
    @GetMapping(value = "/getOrderStatus")
    public OrderEntity getOrderToWechatPayment(@RequestParam("orderSn") String orderSn){

        log.info("****查询订单状态****");
        return orderService.getOrderStatusByOrderSn(orderSn);
    }

    /**
     * 显示用户的所有订单和购物车商品内容
     */
    @ResponseBody
    @PostMapping(value = "/returnOrderPageInfo")
    public R afterAliPaymenReturnOrderPage(@RequestBody Map<String, Object> paramsMap) {

        PageUtils orderPage = orderService.returnOrderPage(paramsMap);
        return R.ok().put("orderPage",orderPage);
    }

}
