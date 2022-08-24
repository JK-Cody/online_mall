package com.mall.sales.houseware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.common.constant.HousewareConstant;
import com.mall.common.constant.OrderStatusEnum;
import com.mall.common.exception.StockException;
import com.mall.common.to.OrderTO;
import com.mall.common.utils.R;
import com.mall.sales.houseware.entity.WareOrderTaskDetailEntity;
import com.mall.sales.houseware.entity.WareOrderTaskEntity;
import com.mall.sales.houseware.feign.OrderFeignService;
import com.mall.sales.houseware.feign.ProductFeignService;
import com.mall.sales.houseware.service.WareOrderTaskDetailService;
import com.mall.sales.houseware.service.WareOrderTaskService;
import com.mall.sales.houseware.to.OrderDetailLockedTO;
import com.mall.sales.houseware.to.WareOrderTaskDetailTO;
import com.mall.sales.houseware.vo.OrderVO;
import com.mall.sales.houseware.vo.SkuHasStock;
import com.mall.sales.houseware.vo.SkuStockLockVO;
import com.mall.sales.houseware.vo.SkuStockVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.houseware.dao.WareSkuDao;
import com.mall.sales.houseware.entity.WareSkuEntity;
import com.mall.sales.houseware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao WareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 按参数查询
     */
    @Override
    public PageUtils anotherQueryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId =(String) params.get("skuId");
        String wareId =(String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 添加已完成的采购单的库存
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveStockInfo(Long skuId, Long wareId, Integer skuNum) {
//添加商品库存
        //查询是否已有创建
        List<WareSkuEntity> entities = WareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId)
                .eq("ware_id", wareId)
        );
        //未创建时
        if (entities ==null || entities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0); //非必要
            //保存采购单对应的sku的名称
            //TODO 使用try-catch来避免查询不到名称而需要回滚的情况
            try {
                R info = productFeignService.info(skuId);
                //将SkuInfoEntity转为键值对
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                //获取sku名称
                if (info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            }catch (Exception e){
                log.info("查询不到sku名称");
            }
            //增加库存
            WareSkuDao.insert(wareSkuEntity);
        //已创建时
        }else {
            //增加库存，自定义sql
            WareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }

    /**
     * 判断Sku库存是否存在的结果列表
     */
    @Override
    public List<SkuHasStock> getSkusHasStock(List<Long> skuIds) {

        List<SkuHasStock> collect = skuIds.stream().map(skuId -> {
            SkuHasStock vo = new SkuHasStock();
            //查询可用的库存数量(减掉锁定库存)
            Long count = this.baseMapper.getSkuStockAvailable(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count !=null && count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 锁定订单任务的sku库存
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean getSkuStockLock(SkuStockLockVO skuStockLockVO) {

        WareOrderTaskEntity wareOrderTaskEntity = getWareOrderTaskSave(skuStockLockVO.getOrderSn());
//查询sku的库存信息
        List<SkuStockVO> collect = skuStockLockVO.getCartItemVOList().stream().map(item -> {
            SkuStockVO skuStockVO = new SkuStockVO();
            Long skuId = item.getSkuId();
            skuStockVO.setSkuId(skuId);
            skuStockVO.setNumber(item.getCount());
            //查询仓库id列表
            List<Long> wareIds = this.getSkuHousewareIds(skuId);
            skuStockVO.setHousewareIdList(wareIds);
            return skuStockVO;
        }).collect(Collectors.toList());
//锁定sku的库存
        for (SkuStockVO skuStockVO : collect) {
            Long skuId = skuStockVO.getSkuId();
            List<Long> wareIds = skuStockVO.getHousewareIdList();
            if (wareIds == null|| wareIds.size() == 0) {
                throw new StockException(skuId);
            }
            //标识每个仓库的库存锁定的结果
            Boolean skuStockResult = false;
            for (Long wareId : wareIds) {
                Long lockStock = this.getSkuStockBeingLock(skuId,wareId,skuStockVO.getNumber());
                //锁定成功时
                if(lockStock == 1){
//保存订单任务的库存工作单
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity().builder()
                            .skuId(skuId)
                            .skuName("") //略过
                            .skuNum(skuStockVO.getNumber())
                            .taskId(wareOrderTaskEntity.getId())
                            .wareId(wareId)
                            .lockStatus(HousewareConstant.WareOrderTaskDetailLockStatusEnum.BEINGLOCKED.getCode()) //设置已锁定
                            .build();
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    OrderDetailLockedTO orderDetailLockedTO = new OrderDetailLockedTO();
                    orderDetailLockedTO.setId(wareOrderTaskDetailEntity.getId());
                    WareOrderTaskDetailTO wareOrderTaskDetailTO = new WareOrderTaskDetailTO();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, wareOrderTaskDetailTO);
                    orderDetailLockedTO.setWareOrderTaskDetailTO(wareOrderTaskDetailTO);
                    //发送库存锁定的消息到库存模块
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked" , orderDetailLockedTO);
                    skuStockResult=true;
                    break;
                }else{
                    //TODO 在其他仓库锁定sku数量
                }
            }
            //存在未锁定情况时，事务回调
            if(!skuStockResult){
                throw new StockException(skuId);
            }
        }
        return true;
    }

    /**
     * 查询仓库id列表
     */
    @Override
    public List<Long> getSkuHousewareIds(Long skuId) {

        return baseMapper.getSkuHousewareIds(skuId);
    }

    /**
     * 工作单状态改变时，解锁sku的库存
     * 受监听器监视
     */
    @Override
    public void getSkuStockUnlock(OrderDetailLockedTO orderDetailLockedTO) {
//获取订单任务的库存工作单
        WareOrderTaskDetailTO wareOrderTaskDetailTO = orderDetailLockedTO.getWareOrderTaskDetailTO();
        Long detailId = orderDetailLockedTO.getId();
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if (wareOrderTaskDetailEntity != null) {
//获取单任务的配送单
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(orderDetailLockedTO.getWareOrderTaskDetailTO().getTaskId());
//根据订单号查询订单
            String orderSn = wareOrderTaskEntity.getOrderSn();
            R result = orderFeignService.getOrderStatus(orderSn);
            if (result.getCode() == 0) {
                OrderVO orderVO = result.getData("orderEntity", new TypeReference<OrderVO>() {
                });
                //订单不存在或状态为已取消时
                if (orderVO == null || Objects.equals(orderVO.getStatus(), OrderStatusEnum.CANCLED.getCode())) {
//解锁订单
                    //确认订单处于锁定状态
                    /* 由于RabbitMqListenerForHouseware有两个消息序列时同步执行，所以都会执行stockUnLock方法来解锁库存，需根据状态来避免重复扣减数量 */
                    if (wareOrderTaskDetailEntity.getLockStatus() == HousewareConstant.WareOrderTaskDetailLockStatusEnum.BEINGLOCKED.getCode()) {
                        log.info("******工作单状态改变时，解锁sku的库存******");
                        this.stockUnLock(wareOrderTaskDetailTO.getSkuId(), wareOrderTaskDetailTO.getWareId(), wareOrderTaskDetailTO.getSkuNum(), detailId);
                    }
                }
            } else {
                throw new RuntimeException("仓库远程服务失败");
            }
        } else {
            log.info("*****数据库没有该库存工作单******");
        }
    }


    /**
     * 在订单关闭时，解锁sku的库存
     * 受监听器监视
     */
    @Transactional
    @Override
    public void getSkuStockUnlock(OrderTO orderTO) {

        WareOrderTaskEntity orderTaskStatusByOrderSn = wareOrderTaskService.getOrderTaskStatusByOrderSn(orderTO.getOrderSn());
        //查询每个购物车商品的状态
        if(orderTaskStatusByOrderSn!=null){
            List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService.list(
                    new QueryWrapper<WareOrderTaskDetailEntity>()
                            .eq("task_id", orderTaskStatusByOrderSn.getId())
                            //已锁定时
                            .eq("lock_status", HousewareConstant.WareOrderTaskDetailLockStatusEnum.BEINGLOCKED.getCode()));
            //解锁库存
            for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
                //确认订单处于锁定状态
                /* 由于RabbitMqListenerForHouseware有两个消息序列时同步执行，所以都会执行stockUnLock方法来解锁库存，需根据状态来避免重复扣减数量 */
                if (detailEntity.getLockStatus() == HousewareConstant.WareOrderTaskDetailLockStatusEnum.BEINGLOCKED.getCode()) {
                    log.info("*****在订单关闭时，解锁sku的库存*****");
                    this.stockUnLock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum(),detailEntity.getId());
                }
            }
        }
    }


//+++++++++++++++++++++++++++
    /**
     * 根据订单号保存库存工作单
     */
    private WareOrderTaskEntity getWareOrderTaskSave(String orderSn){

        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(orderSn);
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);
        return wareOrderTaskEntity;
    }

    /**
     * 锁定sku的库存
     */
    private Long getSkuStockBeingLock(Long skuId, Long wareId, Integer number) {

        return baseMapper.getSkuStockBeingLock(skuId,wareId,number);
    }

    /**
     * 解锁sku的库存
     */
    private void stockUnLock(Long skuId, Long wareId, Integer number,Long detailId) {
        //解锁sku的库存
        this.baseMapper.stockUnLock(skuId,wareId,number);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        //设置已解锁状态
        entity.setLockStatus(HousewareConstant.WareOrderTaskDetailLockStatusEnum.UNLOCK.getCode());
        wareOrderTaskDetailService.updateById(entity);
    }

}