package com.mall.sales.coupon.service.impl;

import com.mall.common.to.MemberPrice;
import com.mall.common.to.SkuReductionTo;
import com.mall.sales.coupon.entity.MemberPriceEntity;
import com.mall.sales.coupon.entity.SkuLadderEntity;
import com.mall.sales.coupon.service.MemberPriceService;
import com.mall.sales.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.coupon.dao.SkuFullReductionDao;
import com.mall.sales.coupon.entity.SkuFullReductionEntity;
import com.mall.sales.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 保存Sku价格改变信息
     */
    @Override
    public void saveSkuReductionInfo(SkuReductionTo skuReductionTo) {
//保存价格打折信息
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getPriceStatus());
        if (skuReductionTo.getFullCount() > 0) {
             skuLadderService.save(skuLadderEntity);
        }
//保存价格减额信息
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,reductionEntity);
        if (reductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            this.save(reductionEntity);
        }
//保存会员价格
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(skuReductionTo.getSkuId());
            priceEntity.setMemberLevelId(item.getId());
            priceEntity.setMemberLevelName(item.getName());
            priceEntity.setMemberPrice(item.getPrice());
            priceEntity.setAddOther(1);
            return priceEntity;
        }).filter(item->{
            //过滤成价格大于0的商品
            return item.getMemberPrice().compareTo(new BigDecimal("0")) > 0;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}