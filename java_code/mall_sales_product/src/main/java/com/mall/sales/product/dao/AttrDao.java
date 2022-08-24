package com.mall.sales.product.dao;

import com.mall.sales.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    //获取有检索类型的属性的id列表
    List<Long> findAttrIdListMatchSerachType(@Param("attrIds") List<Long> attrIds);
}
