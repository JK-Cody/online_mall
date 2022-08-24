package com.mall.sales.product.dao;

import com.mall.sales.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:25
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateRelationByCatalog( @Param("catId") Long catId, @Param("name") String name);
}
