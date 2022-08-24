package com.mall.sales.houseware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.houseware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils anotherQueryPage(Map<String, Object> params);

}

