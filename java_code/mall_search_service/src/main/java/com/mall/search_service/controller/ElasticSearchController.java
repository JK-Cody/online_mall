package com.mall.search_service.controller;

import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.model.SkuForEsSearchModel;
import com.mall.common.utils.R;
import com.mall.search_service.service.SkuProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSearchController {

    @Autowired
    SkuProductSaveService skuProductSaveService;

    /**
     * 上架sku商品并带ES索引
     */
    @PostMapping("/product")
    public R releaseSku(@RequestBody List<SkuForEsSearchModel> skuForEsSearchModelList) {

        boolean isFailure=false;
        try {
            //添加sku商品的ES索引
            isFailure = skuProductSaveService.productStatusUp(skuForEsSearchModelList);
        } catch (Exception e) {
            log.error("ElasticSearchController商品上架错误:{}",e);
            return R.error(BusinessCodeExceptionEnum.PRODUCT_UP_EXCEPTION.getCode(),BusinessCodeExceptionEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        //成功时
        if (!isFailure){
            return R.ok();
            //不成功时
        }else {
            return R.error(BusinessCodeExceptionEnum.PRODUCT_UP_EXCEPTION.getCode(),BusinessCodeExceptionEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
