package com.mall.sales.seckill.service;

import com.mall.sales.seckill.to.SeckillSkuIRelationDetailTO;

import java.util.List;

public interface SeckillScheduleTaskService {

    void openSeckillSkuInComing3Days();

    List<SeckillSkuIRelationDetailTO> getSeckillSkuListInComing3Days();

    SeckillSkuIRelationDetailTO getSeckillSkuDetailInComing3Days(Long skuId);

    String skuBeingCreateOrderByKillId(String killId, String key, Integer number);
}
