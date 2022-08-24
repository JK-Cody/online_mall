package com.mall.sales.order.feign;

import com.mall.common.to.SpuBoundTo;
import com.mall.common.utils.R;
import com.mall.sales.order.vo.SkuStockLockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("mall-sales-houseware")
public interface HousewareFeignService {

    @PostMapping("/houseware/sku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/houseware/wareinfo/orderFare")
    R getOrderFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/houseware/sku/stockLock")
    R getSkuStockLock(@RequestBody SkuStockLockVO skuStockLockVO);
}
