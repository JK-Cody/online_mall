package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:25
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> getListByBrandId(Long brandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateByBrandId(Long brandId,String brandName);

    void updateByCatalog(Long catId, String catName);

}

