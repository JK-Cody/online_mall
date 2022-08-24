package com.mall.sales.order.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-sales-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/{skuId}")
    R getSpuInfo(@PathVariable("skuId") Long skuId);
}
