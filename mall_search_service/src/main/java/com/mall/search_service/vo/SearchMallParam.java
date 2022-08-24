package com.mall.search_service.vo;

import lombok.Data;

import java.util.List;

/**
 * 搜索页面的全文搜索的对象
 * 封装页面所有可能传递过来的搜素字
 */
@Data
public class SearchMallParam {
    //关键字
    private String keyword;
    //三级分类id
    private Long catalog3Id;
    //排序条件
    private String sort;
    //显示有货
    private Integer hasStock;
    //价格区间
    private String skuPrice;
    //品牌id 可以多选
    private List<Long> brandId;
    //按照属性进行筛选
    private List<String> attrs;
    //页码
    private Integer pageNum=1;  //默认在第一页
    //保存网址查询参数
    private String webQueryString;
}
