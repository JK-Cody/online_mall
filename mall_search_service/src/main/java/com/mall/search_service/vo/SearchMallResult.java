package com.mall.search_service.vo;

import com.mall.common.model.SkuForEsSearchModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用ES搜索参数的结果集，方便分类栏显示
 */
@Slf4j
@Data
public class SearchMallResult {
    //查询到的所有商品的sku信息
    private List<SkuForEsSearchModel> products;
    //当前页码
    private Integer pageNum;
    //总记录数
    private Long total;
    //总页码
    private Integer totalPages;
    //当前页码
    private List<Integer> pageNavs;
    //当前查询所有涉及到的品牌
    private List<BrandVO> brands;
    //当前查询所有涉及到的所有属性
    private List<AttrVO> attrs;
    //当前查询所有涉及到的所有商品分类
    private List<CatalogVO> catalogs;
    //多组展示的面包屑导航数据
    private List<NavVO> navs = new ArrayList<>();
    //所有属性Id
    private List<Long> attrIds = new ArrayList<>();

    /**
     * 面包屑导航数据(属性、品牌)
     */
    @Data
    public static class NavVO {

        private String navValue;
        private String navName;
        private String link;  //跳转网址
    }

    @Data
    public static class BrandVO {

        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVO {

        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVO {

        private Long catalogId;
        private String catalogName;
    }
}
