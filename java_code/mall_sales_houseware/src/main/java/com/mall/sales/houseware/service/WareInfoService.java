package com.mall.sales.houseware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.houseware.entity.WareInfoEntity;
import com.mall.sales.houseware.vo.OrderFareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils anotherQueryPage(Map<String, Object> params);

    OrderFareVo getOrderFare(Long addrId);
}

