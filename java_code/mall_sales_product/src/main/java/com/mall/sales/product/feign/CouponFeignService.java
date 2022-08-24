package com.mall.sales.product.feign;

import com.mall.common.to.SkuReductionTo;
import com.mall.common.to.SpuBoundTo;
import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-sales-coupon")
public interface CouponFeignService {

        @PostMapping("/coupon/spubounds/save")
        R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);  //参数区别于原模块的请求

        @PostMapping("/coupon/skufullreduction/saveInfo")
        R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

}
