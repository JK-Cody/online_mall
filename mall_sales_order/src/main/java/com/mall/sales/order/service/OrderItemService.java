package com.mall.sales.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.order.entity.OrderItemEntity;

import java.util.List;
import java.util.Map;

/**
 * 订单项信息
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 00:39:26
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<Long, List<OrderItemEntity>> getListByOrderSnList(List<Long> orderIdList);
}

