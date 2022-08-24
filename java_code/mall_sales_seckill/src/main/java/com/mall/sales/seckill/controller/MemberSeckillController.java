package com.mall.sales.seckill.controller;

import com.mall.sales.seckill.service.SeckillScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 会员获取秒杀活动的内容
 */
@Controller
public class MemberSeckillController {

    @Autowired
    SeckillScheduleTaskService seckillScheduleTaskService;

    /**
     * 创建秒杀订单
     */
    @GetMapping("/getSeckillSkuBeingCreateOrder")
    public String getSeckillSkuBeingCreateOrder(
                          @RequestParam("killId") String killId,  //由 promotionSessionId+skuId组成
                          @RequestParam("key") String key,  // ramdomCode
                          @RequestParam("number") Integer number,
                          Model model){

        String orderSn = seckillScheduleTaskService.skuBeingCreateOrderByKillId(killId,key,number);
        //返回订单号
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}
