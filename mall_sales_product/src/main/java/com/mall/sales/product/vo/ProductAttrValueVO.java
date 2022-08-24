package com.mall.sales.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 修改spu管理的规格维护（参数规格）
 */
@Data
public class ProductAttrValueVO {

    /**
     * 属性id
     */
    public Long attrId;
    /**
     * 属性名
     */
    public String attrName;
    /**
     * 属性值
     */
    public String attrValue;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】
     */
    public Integer quickShow;

}
