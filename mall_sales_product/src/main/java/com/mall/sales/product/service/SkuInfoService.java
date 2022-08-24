package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.SkuInfoEntity;
import com.mall.sales.product.vo.SkuInfoForItemDetail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils anotherQueryPage(Map<String, Object> params);

    List<SkuInfoEntity> getSkuList(Long spuId);

    SkuInfoForItemDetail getSpuShowInfoWithAsync(Long skuId) throws ExecutionException, InterruptedException;

    List<SkuInfoEntity> getInfoByskuIdList(List<Long> skuIdList);
}

