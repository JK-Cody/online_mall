package com.mall.sales.product.dao;

import com.mall.sales.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.sales.product.vo.ProductAttrValueVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {
    //批量更新部分字段,需要开启&allowMultiQueries
    void updateByEntityList(@Param("spuId") Long spuId, @Param("entityList") List<ProductAttrValueVO> entityList);
}
