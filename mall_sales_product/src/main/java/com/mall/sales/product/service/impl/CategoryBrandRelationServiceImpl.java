package com.mall.sales.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mall.common.utils.R;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.entity.CategoryEntity;
import com.mall.sales.product.service.BrandService;
import com.mall.sales.product.service.CategoryService;
import com.mall.sales.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.CategoryBrandRelationDao;
import com.mall.sales.product.entity.CategoryBrandRelationEntity;
import com.mall.sales.product.service.CategoryBrandRelationService;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    /**
     * 保存
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {

        Long brandId = categoryBrandRelation.getBrandId();
        Long catalogId = categoryBrandRelation.getCatalogId();
        //查询各自id所在表
        BrandEntity brandEntity = brandService.getById(brandId);
        CategoryEntity categoryEntity = categoryService.getById(catalogId);
        //设置品牌名和商品分类名
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatalogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    /**
     * 根据品牌修改
     */
    @Override
    public void updateByBrandId(Long brandId, String brandName) {

        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(brandName);
        //自更新
        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    /**
     *  根据商品分类修改
     */
    @Override
    public void updateByCatalog(Long catId, String catName) {

//        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
//        categoryBrandRelationEntity.setCatalogId(catId);
//        categoryBrandRelationEntity.setCatalogName(catName);
//        //自更新
//        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("catalog_id",catId));

        //使用自定义sql更新
        this.baseMapper.updateRelationByCatalog(catId,catName);
    }

    /**
     * 列表并分页
     */
    @Override
    public PageUtils queryPage(Map<String, Object> mapParams) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(mapParams),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 根据品牌查询列表
     */
    @Override
    public List<CategoryBrandRelationEntity> getListByBrandId(Long brandId) {

        QueryWrapper<CategoryBrandRelationEntity> brandIdResults = new QueryWrapper
                <CategoryBrandRelationEntity>().eq("brand_id", brandId);
        return this.list(brandIdResults);
    }

}