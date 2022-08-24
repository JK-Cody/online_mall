package com.mall.sales.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 00:39:26
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

