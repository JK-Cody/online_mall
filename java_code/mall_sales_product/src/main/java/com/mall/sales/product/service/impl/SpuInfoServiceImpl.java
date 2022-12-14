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
     * ??????Spu??????
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoVO spuInfoVO) {
//??????Spu??????????????? pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        //???????????????
        BeanUtils.copyProperties(spuInfoVO,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(infoEntity);
//??????SPU????????????????????? pms_spu_info_desc
        List<String> decript = spuInfoVO.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        saveSpuInfoDesc.save(descEntity);
//??????????????????????????? pms_spu_images
        List<String> images = spuInfoVO.getImages();
        imagesService.saveImagesBySpuId(infoEntity.getId(),images);
//??????spu?????????????????? pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVO.getBaseAttrs();
        //??????????????????????????????
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
//??????spu?????????????????? ??????mall_sms???sms_spu_bounds
        Bounds bounds = spuInfoVO.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R spuBounds = couponFeignService.saveSpuBounds(spuBoundTo);
        //??????????????????
        if(spuBounds.getCode() != 0){
            log.error("????????????spu??????????????????");
        }
//????????????sku?????????
        List<Skus> skuses = spuInfoVO.getSkus();
        if(skuses !=null && skuses.size()>0){
            skuses.forEach(item -> {
                //??????????????????
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    //???????????????
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //Sku?????????????????? pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //???????????????
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                //sku?????????????????? pms_sku_image
                Long skuId = skuInfoEntity.getSkuId();
                //????????????sku????????????
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //?????????????????????
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                //sku??????????????????????????? pms_sku_sale_attr_value
                List<AttrSimple> attrList = item.getAttr();
                //??????????????????????????????
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                //??????sku?????????????????? mall_sms????????? sms_sku_full_reduction???sms_sku_ladder???sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //???????????????0???????????????
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R skuReduction = couponFeignService.saveSkuReduction(skuReductionTo);
                    //??????????????????
                    if(skuReduction.getCode() != 0){
                        log.error("????????????sku??????????????????");
                }  }
            });
        }
    }

    /**
     * ???????????????
     */
    @Override
    public PageUtils anotherQueryPage(Map<String, Object> params) {
//???????????????
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");  //????????????
        if(!StringUtils.isEmpty(key)){
            //and (sku_id=? or sku_name=?)
            queryWrapper.and( (wrapper)->{
                wrapper.eq("id",key).or().like("spu_name",key);
            });
        }
        //?????????????????????
        String status = (String) params.get("status");
        if (!org.springframework.util.StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }
        //???catalogId??????
        String catalogId = (String) params.get("catalogId");
        if(!StringUtils.isEmpty(catalogId)&&!"0".equalsIgnoreCase(catalogId)){
            queryWrapper.eq("catalog_id",catalogId);
        }
        //???brandId??????
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catalogId)){
            queryWrapper.eq("brand_id",brandId);
        }
//????????????
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * spu?????? ??? ????????????Es??????
     */
    @Transactional
    @Override
    public void getSpuRelease(Long spuId) {
//??????sku??????
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuList(spuId);
        List<Long> skuIds = new ArrayList<>();
        List<Long> skuIdList = skuInfoEntities.stream().map(item->
        {   Long skuId = item.getSkuId();
            skuIds.add(skuId);
            return skuId;
        }).collect(Collectors.toList());
//???????????????????????????????????????
        List<ProductAttrValueEntity> AttrValueEntities = productAttrValueService.getbaseAttrlistforspu(spuId);
        List<Long> collectAttrIds = AttrValueEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //????????????????????????id??????
        List<Long> attrIds = attrService.findAttrIdListMatchSerachType(collectAttrIds);
        Set<Long> hashSet =new HashSet<>(attrIds);
        //???????????????????????????????????????
        List<SkuForEsSearchModel.AttrSimple> attrSimpleList =AttrValueEntities.stream().filter(item -> {
            return hashSet.contains(item.getAttrId());
        }).map(item ->{
            SkuForEsSearchModel.AttrSimple attrSimple = new SkuForEsSearchModel.AttrSimple();
            BeanUtils.copyProperties(item,attrSimple);
            return attrSimple;
        }).collect(Collectors.toList());
//?????????????????????sku?????????????????????
//        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = skuSaleAttrValueService.getSalesAttrList(skuIds);
//        List<Long> collectSaleAttrIds = skuSaleAttrValueEntityList.stream().map(item -> {
//            return item.getAttrId();
//        }).collect(Collectors.toList());
//        //????????????????????????id??????
//        List<Long> salesAttrIds = attrService.findAttrIdListMatchSerachType(collectSaleAttrIds);
//        Set<Long> anotherHashSet =new HashSet<>(salesAttrIds);
//        //???????????????????????????????????????
//        List<SkuForEsSearchModel.AttrSimple> salesAttrSimpleList =skuSaleAttrValueEntityList.stream().filter(item -> {
//            return anotherHashSet.contains(item.getAttrId());
//        }).map(item ->{
//            SkuForEsSearchModel.AttrSimple salesAttrSimple = new SkuForEsSearchModel.AttrSimple();
//            BeanUtils.copyProperties(item,salesAttrSimple);
//            return salesAttrSimple;
//        }).collect(Collectors.toList());
//????????????????????????
        Map<Long, Boolean> booleanMap = null;
        //????????????????????????????????????
        try{
            //map<skusHasStockList,{SkuId???HasStock}> ?????? List<SkuHasStock>
            R skusHasStock = housewareFeignService.getSkusHasStock(skuIdList);
            TypeReference<List<SkuHasStock>> typeReference = new TypeReference<List<SkuHasStock>>() { };
            //??????list<SkuHasStock> ??????map<SkuId???HasStock>
            booleanMap = skusHasStock.getData("skusHasStockList",typeReference).stream().collect(Collectors.toMap(SkuHasStock::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("housewareFeignService????????????,????????????"+e);
        }
//??????sku???????????????Es?????????????????????
        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuForEsSearchModel> collect = skuInfoEntities.stream().map(item -> {
            SkuForEsSearchModel skuForEsSearchModel = new SkuForEsSearchModel();
            //???????????????????????????
            BeanUtils.copyProperties(item, skuForEsSearchModel);
            //???????????????????????????
            skuForEsSearchModel.setSkuPrice(item.getPrice());
            skuForEsSearchModel.setSkuImg(item.getSkuDefaultImg());

            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            skuForEsSearchModel.setBrandName(brandEntity.getName());
            skuForEsSearchModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            skuForEsSearchModel.setCatalogName(categoryEntity.getName());
            //??????spu????????????
            skuForEsSearchModel.setAttrs(attrSimpleList);
            //??????sku????????????
//            skuForEsSearchModel.getAttrs().addAll(salesAttrSimpleList);
//????????????
            skuForEsSearchModel.setHotScore(0L);
//????????????
            //?????????????????????????????????true
            if(finalBooleanMap == null){
                skuForEsSearchModel.setHasStock(true);
            }else {
                skuForEsSearchModel.setHasStock(finalBooleanMap.get(item.getSkuId()));
            }
            return skuForEsSearchModel;
        }).collect(Collectors.toList());
//??????????????????ES????????????
        R result = esSearchFeignService.releaseSku(collect);
        if(result.getCode() == 0){
            UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>().eq(
                    "id", spuId).set("publish_status", ProductConstant.PublishStatusEnum.STATUS_PUBLISH.getCode());
            this.baseMapper.update(null,updateWrapper);
        }else {
            //TODO ???????????????
        }
    }

    /**
     * ??????skuId??????Spu
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        return this.getById(spuId);
    }

}