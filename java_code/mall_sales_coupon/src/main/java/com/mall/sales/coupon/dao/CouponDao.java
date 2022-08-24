package com.mall.sales.coupon.dao;

import com.mall.sales.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-12 19:07:43
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
