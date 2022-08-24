package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.AttrEntity;
import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrResultVO;
import com.mall.sales.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrVO(AttrVO attrVO);

    PageUtils getAttrResultVOListPage(Map<String, Object> params, Long catalogId, String attrType);

    List<AttrEntity> getAttrEntityList(Long attrgroupId);

    PageUtils getAttrEntityListWithoutRelationPage(Map<String, Object> params, Long attrgroupId);

    AttrResultVO getAttrResultVO(Long attrId);

    void updateAttrVO(AttrVO attrVO);

    boolean removeAttrVOByIds(List<Long> attrIdsList);

    List<Long> findAttrIdListMatchSerachType(List<Long> ids);
}

