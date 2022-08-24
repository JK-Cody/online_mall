package com.mall.sales.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.common.utils.R;
import com.mall.sales.product.entity.*;
import com.mall.sales.product.feign.SeckillFeignService;
import com.mall.sales.product.service.*;
import com.mall.sales.product.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.SkuInfoDao;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    SeckillFeignService seckillFeignService;

    /**
     * 分页
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 按参数查询并分页
     */
    @Override
    public PageUtils anotherQueryPage(Map<String, Object> params) {
//按查询条件
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        //按catalogId查询
        String catalogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)) {

            queryWrapper.eq("catalog_id", catalogId);
        }
        //按brandId查询
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catalogId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        //按价格区间查询
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            //不输入价格查询时，跳过查询
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception ignored) {

            }
        }
//分页设置
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 查询sku列表
     */
    @Override
    public List<SkuInfoEntity> getSkuList(Long spuId) {

        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    /**
     * 进入SPU单独的介绍页面后，展示所有可选择的sku
     * 异步获取
     */
    @Override
    public SkuInfoForItemDetail getSpuShowInfoWithAsync(Long skuId)  throws ExecutionException, InterruptedException{
//异步保存sku介绍信息
        SkuInfoForItemDetail skuInfoForItemDetail = new SkuInfoForItemDetail();
        //保存sku信息
        CompletableFuture<SkuInfoEntity> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
            skuInfoForItemDetail.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);
        //保存sku销售属性列表
        CompletableFuture<Void> saleAttrVOCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((item) -> {
            List<SaleAttrs> salesAttrList = skuSaleAttrValueService.getSalesAttrList(item.getSpuId());
            skuInfoForItemDetail.setSaleAttrList(salesAttrList);
        }, executor);
        //保存SPU规格参数和属性分组
        CompletableFuture<Void> attrSimpleListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((item) -> {
            //获取规格参数的属性分组名
            List<AttrSimpleWithAttrGroup> attrSimpleWithAttrGroup = attrAttrgroupRelationService.getAttrSimpleWithAttrGroup(item.getSpuId());
            skuInfoForItemDetail.setAttrSimpleWithAttrGroups(attrSimpleWithAttrGroup);
        }, executor);
        //保存spu介绍
        CompletableFuture<Void> spuInfoDescCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((item) -> {
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(item.getSpuId());
            skuInfoForItemDetail.setSpuInfoDescEntity(spuInfoDescEntity);
        }, executor);
        //保存sku图片
        CompletableFuture<Void> skuImagesCompletableFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> imagesServiceList = skuImagesService.getListBySkuId(skuId);
            skuInfoForItemDetail.setSkuImageList(imagesServiceList);
        }, executor);
        //保存sku的参与的秒杀活动(最近三天)
        CompletableFuture<Void> getSeckillSkuDetailInComing3DaysCompletableFuture = CompletableFuture.runAsync(() -> {
            R seckillDetailInComing3Day = seckillFeignService.getSeckillSkuDetailInComing3Days(skuId);
            if(seckillDetailInComing3Day.getCode()==0){
                SeckillSkuDetailVO seckillSkuDetail = seckillDetailInComing3Day.getData(new TypeReference<SeckillSkuDetailVO>() {
                });
                skuInfoForItemDetail.setSeckillSkuDetail(seckillSkuDetail);
            }
        }, executor);
//等待全部完成的结果
        CompletableFuture.allOf(saleAttrVOCompletableFuture,attrSimpleListCompletableFuture, spuInfoDescCompletableFuture,skuImagesCompletableFuture,getSeckillSkuDetailInComing3DaysCompletableFuture).get();
        return skuInfoForItemDetail;
    }

    /**
     * 批量查詢
     */
    @Override
    public List<SkuInfoEntity> getInfoByskuIdList(List<Long> skuIdList) {

        List<SkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<SkuInfoEntity>().in("sku_id", skuIdList));
        return skuInfoEntities;
    }

}