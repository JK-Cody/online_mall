package com.mall.sales.member.web;

import com.alibaba.fastjson.JSON;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;
import com.mall.sales.member.feign.OrderFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 从order模块页面跳转到其他模块
 */
@Slf4j
@Controller
public class JumpPageController {

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 支付成功时的回调地址,并分页
     */
    @GetMapping("/memberOrder.html")
    public String returnMemberOrderPage(@RequestParam(value = "pageNumber",required = false,defaultValue = "0") Integer pageNumber, Model model) throws ExecutionException, InterruptedException {

        Map<String, Object> params = new HashMap<>();
        //设置每页显示多少
        params.put("orderPage", pageNumber.toString());
        //获取购物车商品内容并分页
        R memberOrderInfo = orderFeignService.afterAliPaymenReturnOrderPage(params);
//        System.out.println("memberOrderInfo===============:"+JSON.toJSONString(memberOrderInfo));
        model.addAttribute("memberOrderInfo", memberOrderInfo);
        return "orderList";  //返回页面
    }

}
