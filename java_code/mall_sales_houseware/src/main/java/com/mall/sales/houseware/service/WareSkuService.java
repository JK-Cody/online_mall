package com.mall.sales.houseware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.to.OrderTO;
import com.mall.common.utils.PageUtils;
import com.mall.sales.houseware.entity.WareSkuEntity;
import com.mall.sales.houseware.to.OrderDetailLockedTO;
import com.mall.sales.houseware.vo.SkuHasStock;
import com.mall.sales.houseware.vo.SkuStockLockVO;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils anotherQueryPage(Map<String, Object> params);

    void saveStockInfo(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStock> getSkusHasStock(List<Long> skuIds);

    Boolean getSkuStockLock(SkuStockLockVO skuStockLockVO);

    List<Long> getSkuHousewareIds(Long skuId);

    void getSkuStockUnlock(OrderDetailLockedTO orderDetailLockedTO);

    void getSkuStockUnlock(OrderTO orderTO);
}

