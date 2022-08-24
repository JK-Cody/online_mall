package com.mall.sales.product.service.impl;

import com.mall.sales.product.service.SkuInfoService;
import com.mall.sales.product.vo.SaleAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.SkuSaleAttrValueDao;
import com.mall.sales.product.entity.SkuSaleAttrValueEntity;
import com.mall.sales.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 分页查询
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量获取属性列表
     */
    @Override
    public List<SaleAttrs> getSalesAttrListBySkuIds(List<Long> skuIds) {

        return baseMapper.getSalesAttrListByIds(skuIds);
    }


    /**
     * 根据spuId获取所有的属性列表
     */
    @Override
    public List<SaleAttrs> getSalesAttrList(Long spuId) {

        List<SaleAttrs> salesAttrListByIds = this.baseMapper.getSalesAttrListByIds(spuId);
        return salesAttrListByIds;
    }

    /**
     * 根据skuId获取属性列表
     */
    @Override
    public List<String> getSaleAttrValuelistBySkuId(Long skuId) {

        return this.baseMapper.getSaleAttrValuelistBySkuId(skuId);
    }


}