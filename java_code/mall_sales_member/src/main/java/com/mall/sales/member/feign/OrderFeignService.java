package com.mall.sales.member.feign;

import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("mall-sales-order")
public interface OrderFeignService {

    @PostMapping("/order/paymentinfo/returnOrderPageInfo")
    R afterAliPaymenReturnOrderPage(@RequestBody Map<String, Object> paramsMap);
}
