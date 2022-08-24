package com.mall.sales.houseware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mall.sales.houseware.vo.MergeMultiplePurchaseDetail;
import com.mall.sales.houseware.vo.PurchaseDone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.houseware.entity.PurchaseEntity;
import com.mall.sales.houseware.service.PurchaseService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 采购单信息
 */
@RestController
    @RequestMapping("houseware/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchaseService.save(purchase);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
        purchaseService.updateById(purchase);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 获取单个采购单信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){

        PurchaseEntity purchase = purchaseService.getById(id);
        return R.ok().put("purchase", purchase);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 返回采购单已完成结果
     */
    @PostMapping("/done")
    public R returnDoneResult(@RequestBody PurchaseDone purchaseDone){

        purchaseService.returnPurchaseOrderDoneResult(purchaseDone);
        return R.ok();
    }

    /**
     * 采购人员领取(多个)采购单
     */
    @PostMapping("/received")
    public R receivedOrder(@RequestBody List<Long> orderIds) {

        purchaseService.receivedOrder(orderIds);
        return R.ok();
    }

    /**
     * 合并多个采购需求成为采购单
     */
    @PostMapping("/merge")
    public R mergePurchaseNeedIntoOrder(@RequestBody MergeMultiplePurchaseDetail mergeMultiplePurchaseDetail) {

        purchaseService.mergePurchaseNeedIntoOrder(mergeMultiplePurchaseDetail);
        return R.ok();
    }

    /**
     * 获取未领取(新建 / 已分配)的订单并分页
     */
    @RequestMapping("/unreceive/list")
    public R getUnreceivePurchaseOrder(@RequestParam Map<String, Object> params){

        PageUtils page = purchaseService.getUnreceivePurchaseOrder(params);
        return R.ok().put("page", page);
    }



}
