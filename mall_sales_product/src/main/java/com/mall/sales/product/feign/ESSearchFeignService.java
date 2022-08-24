package com.mall.sales.product.feign;

import com.mall.common.model.SkuForEsSearchModel;
import com.mall.common.to.SkuReductionTo;
import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-sales-product")
public interface ESSearchFeignService {

    @PostMapping("/search/save/product")
    R releaseSku(@RequestBody List<SkuForEsSearchModel> skuForEsSearchModelList);
}
