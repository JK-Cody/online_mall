package com.mall.sales.coupon.service.impl;

import com.mall.sales.coupon.entity.SeckillSkuRelationEntity;
import com.mall.sales.coupon.service.SeckillSkuRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.coupon.dao.SeckillSessionDao;
import com.mall.sales.coupon.entity.SeckillSessionEntity;
import com.mall.sales.coupon.service.SeckillSessionService;

import javax.xml.crypto.Data;

@Slf4j
@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取最近三天秒杀活动场次(数据库sms_seckill_session已录入)
     */
    @Override
    public List<SeckillSessionEntity> getSeckillSessionInComing3Days() {

        List<SeckillSessionEntity> resultList=new ArrayList<>();
        //获取已录入的秒杀活动场次
        List<SeckillSessionEntity> seckillSessionList = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", this.getStartTimeFromNow(), this.getEndTimeFromNow()));
        if(seckillSessionList !=null && seckillSessionList.size()>0){
            //获取秒杀活动关联的商品内容
            List<Long> idList = seckillSessionList.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<SeckillSkuRelationEntity> SeckillSkuRelationList = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", idList));
            //匹配每个场次对应的sku商品列表
            for (SeckillSessionEntity seckillSessionEntity : seckillSessionList) {
                List<SeckillSkuRelationEntity> matchList = SeckillSkuRelationList.stream().filter(item -> item.getPromotionSessionId() == seckillSessionEntity.getId()).collect(Collectors.toList());
                seckillSessionEntity.setSeckillSkuRelationList(matchList);
                resultList.add(seckillSessionEntity);
                SeckillSkuRelationList.removeAll(matchList);
            }
            return resultList;
        }else{
            log.info("未匹配到符合的活动时间");
            return null;
        }
    }


//+++++++++++++++++++++++++++
    /**
     * 计算开始时间
     */
    private String getStartTimeFromNow(){

        LocalDate nowDate = LocalDate.now();
        LocalTime minTime = LocalTime.MIN;
        LocalDateTime of = LocalDateTime.of(nowDate, minTime);
        //设置显示格式
        return of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 计算结束时间
     */
    private String getEndTimeFromNow(){
        //当前日期+2
        LocalDate nowDate = LocalDate.now().plusDays(2);
        LocalTime maxTime = LocalTime.MAX;
        LocalDateTime of = LocalDateTime.of(nowDate, maxTime);
        //设置显示格式
        return of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}