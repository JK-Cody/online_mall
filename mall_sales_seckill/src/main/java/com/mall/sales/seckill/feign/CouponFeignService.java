package com.mall.sales.seckill.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("mall-sales-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/getSeckillSessionInComing3Days")
    R getSeckillSessionInComing3Days();
}
