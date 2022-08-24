package com.mall.sales.coupon.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.coupon.dao.SeckillSkuRelationDao;
import com.mall.sales.coupon.entity.SeckillSkuRelationEntity;
import com.mall.sales.coupon.service.SeckillSkuRelationService;

@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    /**
     * 按参数查询并分页
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SeckillSkuRelationEntity> queryWrapper =new QueryWrapper<>();
        //按秒杀活动场次Id查询
        String promotionSessionId = (String) params.get("promotionSessionId");
        if( ! StringUtils.isEmpty( promotionSessionId)){
            queryWrapper.eq("promotion_session_id", promotionSessionId);
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

}