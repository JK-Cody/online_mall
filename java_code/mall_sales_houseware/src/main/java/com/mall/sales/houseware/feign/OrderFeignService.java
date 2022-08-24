package com.mall.sales.houseware.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-sales-order")
public interface OrderFeignService {

    @GetMapping(value = "/order/order/orderInfo/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
