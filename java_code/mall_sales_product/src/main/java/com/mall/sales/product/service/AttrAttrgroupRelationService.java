package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.AttrAttrgroupRelationEntity;
import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchRelationVO(List<AttrAttrgroupRelationVO> attrAttrgroupRelationVO);

    void deleteBatchRelationVO(AttrAttrgroupRelationVO[] vos);

    void deleteBatchEntity(List<AttrAttrgroupRelationEntity> entities);

    List<AttrSimpleWithAttrGroup> getAttrSimpleWithAttrGroup(Long spuId);
}

