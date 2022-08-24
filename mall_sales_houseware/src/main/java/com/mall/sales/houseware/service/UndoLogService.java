package com.mall.sales.houseware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.houseware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

