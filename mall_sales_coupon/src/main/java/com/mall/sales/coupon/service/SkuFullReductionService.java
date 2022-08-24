package com.mall.sales.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.to.SkuReductionTo;
import com.mall.common.utils.PageUtils;
import com.mall.sales.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-12 19:07:43
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReductionInfo(SkuReductionTo skuReductionTo);
}

