package com.mall.sales.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.common.constant.ProductConstant;
import com.mall.common.model.SkuForEsSearchModel;
import com.mall.common.to.SkuReductionTo;
import com.mall.common.to.SpuBoundTo;
import com.mall.common.utils.R;
import com.mall.common.vo.SkuHasStock;
import com.mall.sales.product.entity.*;
import com.mall.sales.product.feign.CouponFeignService;
import com.mall.sales.product.feign.ESSearchFeignService;
import com.mall.sales.product.feign.HousewareFeignService;
import com.mall.sales.product.service.*;
import com.mall.sales.product.vo.*;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService saveSpuInfoDesc;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    HousewareFeignService housewareFeignService;

    @Autowired
    ESSearchFeignService esSearchFeignService;

    /**
     * 保存Spu信息
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoVO spuInfoVO) {
//保存Spu基本信息到 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        //实体类转换
        BeanUtils.copyProperties(spuInfoVO,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(infoEntity);
//保存SPU商品描述信息到 pms_spu_info_desc
        List<String> decript = spuInfoVO.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        saveSpuInfoDesc.save(descEntity);
//保存图片集合地址到 pms_spu_images
        List<String> images = spuInfoVO.getImages();
        imagesService.saveImagesBySpuId(infoEntity.getId(),images);
//保存spu的规格参数到 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVO.getBaseAttrs();
        //循环保存规格参数列表
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(id.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(infoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
//保存spu的价格信息到 模块mall_sms的sms_spu_bounds
        Bounds bounds = spuInfoVO.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R spuBounds = couponFeignService.saveSpuBounds(spuBoundTo);
        //获取保存结果
        if(spuBounds.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }
//保存所有sku信息到
        List<Skus> skuses = spuInfoVO.getSkus();
        if(skuses !=null && skuses.size()>0){
            skuses.forEach(item -> {
                //图片地址保存
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    //有默认图时
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //Sku基本信息保存 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //实体类转换
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                //sku图片信息保存 pms_sku_image
                Long skuId = skuInfoEntity.getSkuId();
                //循环保存sku图片信息
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //过滤无图片地址
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                //sku销售属性信息保存到 pms_sku_sale_attr_value
                List<AttrSimple> attrList = item.getAttr();
                //循环保存销售属性列表
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                //保存sku的优惠信息到 mall_sms模块的 sms_sku_full_reduction、sms_sku_ladder、sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //剔除价格比0更小的对象
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R skuReduction = couponFeignService.saveSkuReduction(skuReductionTo);
                    //获取保存结果
                    if(skuReduction.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                }  }
            });
        }
    }

    /**
     * 按参数查询
     */
    @Override
    public PageUtils anotherQueryPage(Map<String, Object> params) {
//按查询条件
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");  //查询条件
        if(!StringUtils.isEmpty(key)){
            //and (sku_id=? or sku_name=?)
            queryWrapper.and( (wrapper)->{
                wrapper.eq("id",key).or().like("spu_name",key);
            });
        }
        //按创建状态查询
        String status = (String) params.get("status");
        if (!org.springframework.util.StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }
        //按catalogId查询
        String catalogId = (String) params.get("catalogId");
        if(!StringUtils.isEmpty(catalogId)&&!"0".equalsIgnoreCase(catalogId)){
            queryWrapper.eq("catalog_id",catalogId);
        }
        //按brandId查询
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catalogId)){
            queryWrapper.eq("brand_id",brandId);
        }
//分页设置
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * spu发布 和 并保存到Es索引
     */
    @Transactional
    @Override
    public void getSpuRelease(Long spuId) {
//获取sku列表
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuList(spuId);
        List<Long> skuIds = new ArrayList<>();
        List<Long> skuIdList = skuInfoEntities.stream().map(item->
        {   Long skuId = item.getSkuId();
            skuIds.add(skuId);
            return skuId;
        }).collect(Collectors.toList());
//获取可供检索的参数规格列表
        List<ProductAttrValueEntity> AttrValueEntities = productAttrValueService.getbaseAttrlistforspu(spuId);
        List<Long> collectAttrIds = AttrValueEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //获取带检索类型的id集合
        List<Long> attrIds = attrService.findAttrIdListMatchSerachType(collectAttrIds);
        Set<Long> hashSet =new HashSet<>(attrIds);
        //获取可供检索的参数规格列表
        List<SkuForEsSearchModel.AttrSimple> attrSimpleList =AttrValueEntities.stream().filter(item -> {
            return hashSet.contains(item.getAttrId());
        }).map(item ->{
            SkuForEsSearchModel.AttrSimple attrSimple = new SkuForEsSearchModel.AttrSimple();
            BeanUtils.copyProperties(item,attrSimple);
            return attrSimple;
        }).collect(Collectors.toList());
//获取可供检索的sku的销售属性列表
//        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = skuSaleAttrValueService.getSalesAttrList(skuIds);
//        List<Long> collectSaleAttrIds = skuSaleAttrValueEntityList.stream().map(item -> {
//            return item.getAttrId();
//        }).collect(Collectors.toList());
//        //获取带检索类型的id集合
//        List<Long> salesAttrIds = attrService.findAttrIdListMatchSerachType(collectSaleAttrIds);
//        Set<Long> anotherHashSet =new HashSet<>(salesAttrIds);
//        //获取可供检索的参数规格列表
//        List<SkuForEsSearchModel.AttrSimple> salesAttrSimpleList =skuSaleAttrValueEntityList.stream().filter(item -> {
//            return anotherHashSet.contains(item.getAttrId());
//        }).map(item ->{
//            SkuForEsSearchModel.AttrSimple salesAttrSimple = new SkuForEsSearchModel.AttrSimple();
//            BeanUtils.copyProperties(item,salesAttrSimple);
//            return salesAttrSimple;
//        }).collect(Collectors.toList());
//获取可调用的库存
        Map<Long, Boolean> booleanMap = null;
        //避免调用失败引起方法回滚
        try{
            //map<skusHasStockList,{SkuId，HasStock}> 转为 List<SkuHasStock>
            R skusHasStock = housewareFeignService.getSkusHasStock(skuIdList);
            TypeReference<List<SkuHasStock>> typeReference = new TypeReference<List<SkuHasStock>>() { };
            //再将list<SkuHasStock> 转为map<SkuId，HasStock>
            booleanMap = skusHasStock.getData("skusHasStockList",typeReference).stream().collect(Collectors.toMap(SkuHasStock::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("housewareFeignService调用失败,出现异常"+e);
        }
//保存sku对象设置成Es检索对象的形式
        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuForEsSearchModel> collect = skuInfoEntities.stream().map(item -> {
            SkuForEsSearchModel skuForEsSearchModel = new SkuForEsSearchModel();
            //保存相同命名的属性
            BeanUtils.copyProperties(item, skuForEsSearchModel);
            //保存不同命名的属性
            skuForEsSearchModel.setSkuPrice(item.getPrice());
            skuForEsSearchModel.setSkuImg(item.getSkuDefaultImg());

            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            skuForEsSearchModel.setBrandName(brandEntity.getName());
            skuForEsSearchModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            skuForEsSearchModel.setCatalogName(categoryEntity.getName());
            //保存spu基本属性
            skuForEsSearchModel.setAttrs(attrSimpleList);
            //保存sku销售属性
//            skuForEsSearchModel.getAttrs().addAll(salesAttrSimpleList);
//热度设置
            skuForEsSearchModel.setHotScore(0L);
//库存设置
            //没有库存数据时，依然为true
            if(finalBooleanMap == null){
                skuForEsSearchModel.setHasStock(true);
            }else {
                skuForEsSearchModel.setHasStock(finalBooleanMap.get(item.getSkuId()));
            }
            return skuForEsSearchModel;
        }).collect(Collectors.toList());
//发送数据进行ES索引添加
        R result = esSearchFeignService.releaseSku(collect);
        if(result.getCode() == 0){
            UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>().eq(
                    "id", spuId).set("publish_status", ProductConstant.PublishStatusEnum.STATUS_PUBLISH.getCode());
            this.baseMapper.update(null,updateWrapper);
        }else {
            //TODO 接口幂等性
        }
    }

    /**
     * 根据skuId获取Spu
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        return this.getById(spuId);
    }

}