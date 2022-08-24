package com.mall.sales.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mall.sales.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * 增加属性列表
 */
@Data
public class AttrGroupVO {

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 保存含有的属性列表(自定义)
     */
    private List<AttrEntity> attrs;
}
