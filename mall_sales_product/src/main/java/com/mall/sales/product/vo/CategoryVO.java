package com.mall.sales.product.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKU 二级和三级目录的联合列表
 */
@Data
public class CategoryVO {

    private Long catId;
    private String name;
    private Long parentCid;
    private Integer catLevel;
    private List<CategoryVO> catalog3List; //三级商品分类列表
}
