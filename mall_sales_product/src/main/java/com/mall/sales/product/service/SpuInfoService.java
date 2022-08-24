package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.SpuInfoEntity;
import com.mall.sales.product.vo.SpuInfoVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    void saveSpuInfo(SpuInfoVO spuInfoVO);

    PageUtils anotherQueryPage(Map<String, Object> params);

    void getSpuRelease(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

