package com.mall.sales.product.dao;

import com.mall.sales.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.sales.product.vo.SaleAttrs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SaleAttrs> getSalesAttrListByIds(@Param("spuId") Long spuId);

    List<SaleAttrs> getSalesAttrListByIds(@Param("skuIds") List<Long> skuIds);

    List<SaleAttrs> getAttrWithSkuId(@Param("spuId") Long spuId);

    List<String> getSaleAttrValuelistBySkuId(@Param("skuId") Long skuId);
}
