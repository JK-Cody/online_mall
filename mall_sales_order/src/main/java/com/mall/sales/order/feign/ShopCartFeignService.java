package com.mall.sales.order.feign;

import com.mall.sales.order.vo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("mall-sales-shop-cart")
public interface ShopCartFeignService {

    @GetMapping("/cartItemList")
    List<CartItemVO> getCartItemList();
}
