package com.mall.sales.houseware.service.impl;

import com.mall.common.constant.HousewareConstant;
import com.mall.sales.houseware.vo.MergeMultiplePurchaseDetail;
import com.mall.sales.houseware.vo.PurchaseItemDone;
import com.mall.sales.houseware.vo.PurchaseDone;
import com.mall.sales.houseware.entity.PurchaseDetailEntity;
import com.mall.sales.houseware.service.PurchaseDetailService;
import com.mall.sales.houseware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.houseware.dao.PurchaseDao;
import com.mall.sales.houseware.entity.PurchaseEntity;
import com.mall.sales.houseware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 参数查询
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询状态并分页
     */
    @Override
    public PageUtils getUnreceivePurchaseOrder(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                //查询状态为新建 或 已分配
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    /**
     * 合并多个采购需求到采购单（独立创建）
     */
    @Transactional
    @Override
    public void mergePurchaseNeedIntoOrder(MergeMultiplePurchaseDetail mergeMultiplePurchaseDetail) {
//获取合并的采购单Id
        Long purchaseId = mergeMultiplePurchaseDetail.getPurchaseId();
        //没有时，创建新采购单
        if(purchaseId == null) {
            PurchaseEntity newPurchaseEntity = this.createpurchaseOrder();
            purchaseId = newPurchaseEntity.getId();
        //采购单状态不为新建 / 已分配时，创建新采购单
        //采购单是独立创建
        } else if(this.getById(purchaseId).getStatus() !=HousewareConstant.PurchaseStatusEnum.CREATED.getCode()
                && this.getById(purchaseId).getStatus() !=HousewareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
            PurchaseEntity newPurchaseEntity = this.createpurchaseOrder();
            purchaseId = newPurchaseEntity.getId();
            log.info("现有采购单状态不是新建/已分配时，使用新采购单号:"+purchaseId);
        }
//更新采购需求列表
        List<Long> items = mergeMultiplePurchaseDetail.getItems();
        //设为final变量
        Long finalPurchaseId = purchaseId;
        //循环采购请求保存信息
        List<PurchaseDetailEntity> collect = items.stream().map(id -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            //保存采购需求Id
            detailEntity.setId(id);
            //保存采购单id
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(HousewareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
//更新采购单
        //覆盖原采购单
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(finalPurchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     * 采购人员领取采购单
     */
    @Override
    public void receivedOrder(List<Long> orderIds) {
//更新采购单
        List<Long> matchOrderIds = new ArrayList<>();
        List<PurchaseEntity> collect = orderIds.stream().map(orderId -> {
            return this.getById(orderId);
            //过滤状态为 新建 / 已分配的采购单
        }).filter(status -> {
            return status.getStatus() == HousewareConstant.PurchaseStatusEnum.CREATED.getCode() || status.getStatus() == HousewareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
            //置领取状态
        }).peek(item->{
                item.setStatus(HousewareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                item.setUpdateTime(new Date());
                //保存符合的采购单id
                matchOrderIds.add(item.getId());
        }).collect(Collectors.toList());
        this.updateBatchById(collect);
//更新采购需求列表
        if(matchOrderIds.size()!=0){
            List<PurchaseDetailEntity> purchaseDetaillist = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id", matchOrderIds));
            List<PurchaseDetailEntity> updateResult = purchaseDetaillist.stream().map(
                    item -> {
                        item.setStatus(HousewareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                        return item;
                    }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(updateResult);
        }
    }

    /**
     * 返回采购单已完成结果
     */
    @Transactional
    @Override
    public void returnPurchaseOrderDoneResult(PurchaseDone purchaseDone) {
//更新采购需求列表
        Long OrderId = purchaseDone.getId();
        boolean isSucced = true;
        List<PurchaseDetailEntity> detailEntities = new ArrayList<>();
        List<PurchaseItemDone> items = purchaseDone.getItems();
        for (PurchaseItemDone item : items) {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            //设置采购失败状态
            if(item.getStatus()==HousewareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                isSucced = false;
                entity.setStatus(item.getStatus());
            }else{
                //设置完成状态
                entity.setStatus(HousewareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
//增加已完成的采购单的库存
                PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.saveStockInfo(purchaseDetailEntity.getSkuId(),purchaseDetailEntity.getWareId(),purchaseDetailEntity.getSkuNum());
            }
            entity.setId(item.getItemId());
            detailEntities.add(entity);
        }
        purchaseDetailService.updateBatchById(detailEntities);
//更新采购单
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(OrderId);
        //采购需求有异常状态时，采购单也为异常状态
        purchaseEntity.setStatus(isSucced?HousewareConstant.PurchaseStatusEnum.FINISH.getCode():HousewareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }


//++++++++++++++++++++++++++++++++++++++
    /**
     * 创建新采购单
     */
    private PurchaseEntity createpurchaseOrder(){

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(HousewareConstant.PurchaseDetailStatusEnum.CREATED.getCode());
        purchaseEntity.setCreateTime(new Date());
        purchaseEntity.setUpdateTime(new Date());
        this.save(purchaseEntity);
        return purchaseEntity;
    }

}