package com.mall.sales.product.dao;

import com.mall.sales.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.sales.product.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
    //查询部分字段
    List<CategoryVO> selectPartInfo();
}

