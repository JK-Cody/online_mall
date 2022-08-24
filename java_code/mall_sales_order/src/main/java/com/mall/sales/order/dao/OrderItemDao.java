package com.mall.sales.order.dao;

import com.mall.sales.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单项信息
 * 
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 00:39:26
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

    @MapKey("order_id") //key为orderId
    Map<Long,List<OrderItemEntity>> getListByOrderSnList(@Param("orderIdList") List<Long> orderIdList);
}
