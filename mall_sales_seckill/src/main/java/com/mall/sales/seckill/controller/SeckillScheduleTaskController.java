package com.mall.sales.seckill.controller;

import com.mall.common.utils.R;
import com.mall.sales.seckill.service.SeckillScheduleTaskService;
import com.mall.sales.seckill.to.SeckillSkuIRelationDetailTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取秒杀活动的内容
 */
@RestController
public class SeckillScheduleTaskController {

    @Autowired
    SeckillScheduleTaskService seckillScheduleTaskService;

    /**
     * 获取最近三天的秒杀活动的所有sku内容
     */
    @GetMapping(value = "/getSeckillSkuListInComing3Days")
    public R getSeckillSkuListInComing3Days() {

        List<SeckillSkuIRelationDetailTO> vos = seckillScheduleTaskService.getSeckillSkuListInComing3Days();
        return R.ok().setData(vos);
    }

    /**
     * 获取最近三天的秒杀活动的单个sku内容
     */
    @GetMapping(value = "/getSeckillSkuDetailInComing3Days/{skuId}")
    public R getSeckillSkuDetailInComing3Days(@PathVariable("skuId") Long skuId) {

        SeckillSkuIRelationDetailTO detail = seckillScheduleTaskService.getSeckillSkuDetailInComing3Days(skuId);
        return R.ok().setData(detail);
    }

}
