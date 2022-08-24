package com.mall.sales.product.vo;

import com.mall.sales.product.entity.SkuImagesEntity;
import com.mall.sales.product.entity.SkuInfoEntity;
import com.mall.sales.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * SKU商品详细页面对象
 */
@Data
public class SkuInfoForItemDetail {

    SkuInfoEntity info;
    boolean hasStock = true;//库存
    List<SkuImagesEntity> skuImageList;
    List<SaleAttrs> saleAttrList;  //销售属性列表
    SpuInfoDescEntity spuInfoDescEntity;    //SPU描述信息
    List<AttrSimpleWithAttrGroup> attrSimpleWithAttrGroups;  //规格参数和属性分组
    SeckillSkuDetailVO seckillSkuDetail;  //秒杀产品信息

}
