package com.mall.sales.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.sales.product.entity.CategoryBrandRelationEntity;
import com.mall.sales.product.service.AttrService;
import com.mall.sales.product.service.CategoryBrandRelationService;
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

import com.mall.sales.product.dao.BrandDao;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 条件查询带分页
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key = (String) params .get("key");  //key是点击的商品
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if( !StringUtils. isEmpty(key)){
            queryWrapper.eq("brand_id", key) .or().like( "name" , key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 更新pms_brand表和其他表
     * @param brand
     */
    @Override
    public void updateTable(BrandEntity brand) {

        this.updateById(brand);  //自更新
        Long brandId = brand.getBrandId();
        String name = brand.getName();

        if(!StringUtils.isEmpty(name)){
            //更新pms_category_brand_relation表
            categoryBrandRelationService.updateByBrandId(brandId,name);
        }
    }

    /**
     * 批量获取品牌列表
     */
    @Override
    public List<BrandEntity> getBrandListByIds(List<Long> brandIds) {

        return baseMapper.selectBatchIds(brandIds);
    }

    /**
     * 根据商品分类查询列表
     */
    @Override
    public List<BrandEntity> getBrandEntityBycatId(Long catId) {
        //查询品牌列表
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catalog_id", catId));
        List<Long> collect = list.stream().map(
                CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        return this.list(new QueryWrapper<BrandEntity>().in("brand_id", collect));
    }

    /**
     * 实体类列表转为VO列表
     */
    @Override
    public List<BrandVO> transferToVOList(List<BrandEntity> list) {

        List<BrandVO> collect = list.stream().map(item -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(item.getBrandId());
            brandVO.setBrandName(item.getName());
            return brandVO;
        }).collect(Collectors.toList());
        return collect;
    }
}