package com.mall.sales.houseware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.houseware.vo.MergeMultiplePurchaseDetail;
import com.mall.sales.houseware.vo.PurchaseDone;
import com.mall.sales.houseware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils getUnreceivePurchaseOrder(Map<String, Object> params);

    void mergePurchaseNeedIntoOrder(MergeMultiplePurchaseDetail mergeMultiplePurchaseDetail);

    void receivedOrder(List<Long> orderIds);

    void returnPurchaseOrderDoneResult(PurchaseDone purchaseDone);
}

