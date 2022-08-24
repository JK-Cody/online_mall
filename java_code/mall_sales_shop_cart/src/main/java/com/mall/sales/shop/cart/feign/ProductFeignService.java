package com.mall.sales.shop.cart.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("mall-sales-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/saleAttrValuelist/{skuId}")
    List<String> getSaleAttrValuelist(@RequestParam("skuId") Long skuId);

    @GetMapping("/product/skuinfo/priceinfo/{skuId}")
    BigDecimal getPriceInfo(@PathVariable("skuId") Long skuId);
}
