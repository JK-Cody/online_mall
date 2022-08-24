package com.mall.common.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SkuInfoEntity转为Es检索对象的形式
 */
@Data
public class SkuForEsSearchModel {

    private Long skuId;

    private Long spuId;

    private Long catalogId;

    private Long brandId;

    private Long saleCount;

    private String skuTitle;
//区别部分
    private String catalogName;

    private String brandName;

    private String brandImg;  //pms_brand的logo

    private String skuImg;

    private BigDecimal skuPrice;

    private Boolean hasStock;  //wms_ware_sku的 stock和stock_locked两者差值

    private Long hotScore;  //搜索热度评分

    private List<AttrSimple> attrs;   //自定义属性列表

    @Data
    public static class AttrSimple {

        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
