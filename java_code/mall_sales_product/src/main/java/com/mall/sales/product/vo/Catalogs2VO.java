package com.mall.sales.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKU 二级和三级目录的联合列表
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalogs2VO {

    private String catId;
    private String name;
    private String catalog1Id; //父分类id
    private List<Category3Vo> catalog3List;  //三级商品分类列表

    /**
     * SKU三级商品分类
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Category3Vo{

        private String catId;
        private String name;
        private String catalog2Id;  //父分类id
    }
}
