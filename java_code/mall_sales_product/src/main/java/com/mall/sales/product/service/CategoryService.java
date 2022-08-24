package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.CategoryEntity;
import com.mall.sales.product.vo.Catalogs2VO;
import com.mall.sales.product.vo.CategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeListById(List<Long> asList);

    Long[] getCategoryPath(Long catalogId);

    void updateTable(CategoryEntity category);

    List<CategoryEntity> getLevelOneCategorys();

    Map<String, List<CategoryVO>> getCatalogAllLevel();
}

