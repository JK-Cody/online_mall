package com.mall.sales.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 关联pms_attr_attrgroup_relation
 * 只保留前端需要的属性
 */
@Data
public class AttrAttrgroupRelationVO {

    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性分组id
     */
    private Long attrGroupId;

}
