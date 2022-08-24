package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.AttrEntity;
import com.mall.sales.product.entity.AttrGroupEntity;
import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrGroupVO;
import com.mall.sales.product.vo.AttrSimple;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params,Long catId);

    List<AttrGroupVO> getAttrGroupWithAttrEntityList(Long catalogId);

}

