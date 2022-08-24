package com.mall.search_service.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("mall-sales-product")
public interface ProductFeignService {

    @GetMapping("/product/brand/brandListInfo")
    R getBrandListByIds(@RequestParam("brandIds") List<Long> brandIds) ;
}
