package com.mall.sales.product.vo;

import com.mall.sales.product.vo.AttrSimple;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * SPU规格参数和属性分组
 */
@ToString
@Data
public class AttrSimpleWithAttrGroup {

    private String groupName;
    private List<AttrSimple> attrs;
}
