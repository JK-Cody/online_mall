package com.mall.sales.product.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-sales-houseware")
public interface HousewareFeignService {

    @PostMapping("/houseware/sku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
