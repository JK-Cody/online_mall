package com.mall.sales.seckill.feign;

import com.mall.common.utils.R;
import com.mall.sales.seckill.vo.SkuInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("mall-sales-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/{skuIdList}")
    List<SkuInfoVO> getInfoByskuIdList(@RequestParam(value = "skuIdList", required = true) List<Long> skuIdList);

}
