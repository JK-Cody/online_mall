package com.mall.sales.product.dao;

import com.mall.sales.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
	
}
