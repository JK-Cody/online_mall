package com.mall.sales.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.order.entity.OrderEntity;
import com.mall.sales.order.service.OrderService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 订单
 */
@RestController
@RequestMapping("order/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){

		orderService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/orderInfo/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn){

        OrderEntity orderEntity = orderService.getOrderByOrderSn(orderSn);
        return R.ok().put("orderEntity",orderEntity);
    }

}
