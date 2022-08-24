package com.mall.sales.product.feign;

import com.mall.common.utils.R;
import com.mall.sales.product.fallback.SentinelWithFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-sales-seckill",fallback = SentinelWithFeignFallback.class )
public interface SeckillFeignService {

    @GetMapping(value = "/getSeckillSkuDetailInComing3Days/{skuId}")
    R getSeckillSkuDetailInComing3Days(@PathVariable("skuId") Long skuId);
}
