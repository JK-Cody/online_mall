package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.SkuSaleAttrValueEntity;
import com.mall.sales.product.vo.AttrSimple;
import com.mall.sales.product.vo.SaleAttrs;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SaleAttrs> getSalesAttrListBySkuIds(List<Long> skuIds);

    List<SaleAttrs> getSalesAttrList(Long spuId);

    List<String> getSaleAttrValuelistBySkuId(Long skuId);
}

