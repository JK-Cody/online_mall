package com.mall.sales.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mall.common.constant.SeckillConstant;
import com.mall.common.to.SeckillSkuCreateOrderTO;
import com.mall.common.utils.R;
import com.mall.common.vo.MemberRespVo;
import com.mall.sales.seckill.exception.SentinelBlockHandler;
import com.mall.sales.seckill.feign.CouponFeignService;
import com.mall.sales.seckill.feign.ProductFeignService;
import com.mall.sales.seckill.interceptor.LoginUserInterceptor;
import com.mall.sales.seckill.service.SeckillScheduleTaskService;
import com.mall.sales.seckill.to.SeckillSessionTO;
import com.mall.sales.seckill.to.SeckillSkuIRelationDetailTO;
import com.mall.sales.seckill.to.SeckillSkuRelationTO;
import com.mall.sales.seckill.vo.SkuInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillScheduleTaskServiceImpl implements SeckillScheduleTaskService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 开启最近三天的秒杀活动(数据库sms_seckill_session已录入)
     */
    @Override
    public void openSeckillSkuInComing3Days() {

        log.info("开启最近三天的秒杀活动");
        /* 当时间不匹配时，没有对象返回,需要修改数据库 sms_seckill_session */
        R result = couponFeignService.getSeckillSessionInComing3Days();
        if(result.getCode()==0) {
            List<SeckillSessionTO> seckillSessionList = result.getData("seckillSessionList", new TypeReference<List<SeckillSessionTO>>() {
            });
            //保存秒杀活动的时间轴
            saveTimeRangeAndKillIdList(seckillSessionList);
            //保存秒杀活动的sku内容
            saveSkuRelationList(seckillSessionList);
        }
    }

    /**
     * 获取最近三天的秒杀活动所有的sku内容
     */
//  @SentinelResource(value = "seckillSkuListInComing3Days",blockHandlerClass = SentinelBlockHandler.class ,blockHandler = "seckillSkuListInComing3DaysBlockHandler") //sentinel流控规则名
    @Override
    public List<SeckillSkuIRelationDetailTO> getSeckillSkuListInComing3Days() {
//sentinel定义受保护的资源
          //获取流控规则名
//        try (Entry entry = SphU.entry("seckillSkuListInComing3Days")) {
//获取时间轴缓存
            String timeString=SeckillConstant.selection0fComingThreeDays.TIME_RANGE.getMsg();
            Set<String> keys = stringRedisTemplate.keys( timeString+"*");
            //抽取时间段
            for (String key : keys) {
                String replace = key.replace(timeString, "");
                String[] split = replace.split("_");
                long startTime = Long.parseLong(split[0]);
                long endTime= Long.parseLong(split[1]);
                long time = new Date().getTime();
                if (time >= startTime && time <= endTime) {
//获取活动的sku列表
                    //获取缓存的索引区间
                    List<String> range = stringRedisTemplate.opsForList().range(key, 0, 100);
                    String skuListString=SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg();
                    BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(skuListString);
                    List<String> list = operations.multiGet(range);
                    if (list != null && list.size()>0) {
                        return list.stream().map(item -> {
                            //转为原对象
                            SeckillSkuIRelationDetailTO detail = JSON.parseObject(item, SeckillSkuIRelationDetailTO.class);
                            //清除随机码，不展示在页面
                            detail.setRamdomCode(null);
                            return detail;
                        }).collect(Collectors.toList());
                    }
                    break;
                }
            }
//        } catch (BlockException e) {
//            log.warn("Sentinel服务资源被限流：" + e.getMessage());
//        }
        return null;
    }

    /**
     * 获取最近三天的秒杀活动的单个sku详细内容
     */
    @Override
    public SeckillSkuIRelationDetailTO getSeckillSkuDetailInComing3Days(Long skuId) {
//获取缓存的sku列表
        BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg());
        Set<String> keys = operations.keys();
        if(keys!=null && keys.size()>0){
            for (String key : keys) {
//按照skuId匹配sku
                //匹配键值，如1-1
                if (Pattern.matches("\\d-" + skuId, key)) {
                    //转为原对象
                    String v = operations.get(key);
                    SeckillSkuIRelationDetailTO detail = JSON.parseObject(v, SeckillSkuIRelationDetailTO.class);
                    if (detail != null) {
//时间轴匹配
                        //判断时间轴包含当前时间
                        long current = System.currentTimeMillis();
                        if (detail.getStartTime() < current && detail.getEndTime() > current) {
                            return detail;
                        }
//清除随机码，不展示
                        detail.setRamdomCode(null);
                        return detail;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 创建最近三天的秒杀活动的订单
     * killId是promotionSessionId+skuId组成,key是ramdomCode
     */
    @Override
    public String skuBeingCreateOrderByKillId(String killId, String key, Integer number) {
//获取会员信息
        MemberRespVo MemberRespVo = LoginUserInterceptor.threadLocal.get();
        Long memberId = MemberRespVo.getId();
//获取缓存的sku
        //获取缓存的sku列表
        BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg());
        //匹配对应的sku,如1-1
        String skuListString = operations.get(killId);
        if (!StringUtils.isEmpty(skuListString)) {
            //转为原对象
            SeckillSkuIRelationDetailTO detail = JSONObject.parseObject(skuListString, SeckillSkuIRelationDetailTO.class);
//保存购买数量到缓存
            //时间轴匹配
            long nowTime = new Date().getTime();
            long startTime = detail.getStartTime();
            Long endTime = detail.getEndTime();
            Long ttl=endTime-startTime;
            if( nowTime>=startTime && nowTime<=endTime){
                    //限制数量匹配
                    if(detail.getSeckillLimit()>=number){
                        //使用占位存储，查看是否成功
                        String redisKillId=SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_HAD_CREATE_ORDER.getMsg()+detail.getPromotionSessionId()+"_"+detail.getSkuId();
                        Boolean saveResult = stringRedisTemplate.opsForValue().setIfAbsent(redisKillId, number.toString(), ttl, TimeUnit.MILLISECONDS);//过期时间
                        if(Boolean.TRUE.equals(saveResult)){
//扣减总秒杀数量(信号量)
                            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_TOKEN_SEMAPHORE.getMsg() + detail.getRamdomCode());
                            //尝试性扣减
                            boolean result = semaphore.tryAcquire(number);
//创建秒杀订单
                            //成功时
                            if(result) {
                                String orderSn = IdWorker.getTimeId();
                                SeckillSkuCreateOrderTO seckillSkuCreateOrderTO = new SeckillSkuCreateOrderTO();
                                seckillSkuCreateOrderTO.setMemberId(memberId);
                                seckillSkuCreateOrderTO.setOrderSn(orderSn);
                                seckillSkuCreateOrderTO.setSkuId(detail.getSkuId());
                                seckillSkuCreateOrderTO.setPromotionSessionId(detail.getPromotionSessionId());
                                seckillSkuCreateOrderTO.setSeckillPrice(detail.getSeckillPrice());
                                seckillSkuCreateOrderTO.setSkuCount(number);
                                //发送订单创建消息
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillSkuCreateOrderTO);
                                return orderSn;
                            }
                        }else{
                            String message =  memberId+ redisKillId;
                            log.info("用户重复秒杀："+message);
                            return null;
                        }
                    }
                }else {
                    log.info("不在秒杀时间");
                    return null;
                }
            }else {
                log.info("获取不到killId的缓存");
                return null;
            }
        return null;
    }


//+++++++++++++++++++++++++++++
    /**
     * 保存秒杀活动的时间轴和killId列表
     */
    private void saveTimeRangeAndKillIdList( List<SeckillSessionTO> seckillSessionList){

        seckillSessionList.forEach(item->{
            long startTim = item.getStartTime().getTime();
            long endTime = item.getEndTime().getTime();
            String timeRange= SeckillConstant.selection0fComingThreeDays.TIME_RANGE.getMsg()+startTim+"_"+endTime;
            if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(timeRange))){
                //保存killId列表
                List<String> killIdList = item.getSeckillSkuRelationList().stream().map(
                        //按照promotionSessionId+skuId组成killId
                        skuRelation ->skuRelation.getPromotionSessionId() + "-" +skuRelation.getSkuId().toString()
                ).collect(Collectors.toList());
                //向左边批量保存到缓存
                stringRedisTemplate.opsForList().leftPushAll(timeRange,killIdList);
            }
        });
    }

    /**
     * 保存秒杀活动的所有sku内容
     */
    private void saveSkuRelationList( List<SeckillSessionTO> seckillSessionList) {
//获取秒杀活动的所有sku内容
        List<Long> skuIdList = new ArrayList<>();
        if (seckillSessionList != null) {
            for (SeckillSessionTO seckillSessionTO : seckillSessionList) {
                for (SeckillSkuRelationTO seckillSkuRelationTO : seckillSessionTO.getSeckillSkuRelationList()) {
                    skuIdList.add(seckillSkuRelationTO.getSkuId());
                }
            }
            if (skuIdList.size() > 0) {
                //批量查询
                List<SkuInfoVO> skuInfoList = productFeignService.getInfoByskuIdList(skuIdList);
//保存秒杀活动的所有sku内容
                seckillSessionList.stream().forEach(session -> {
                        String skuKey = SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg();
                        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(skuKey);
                        session.getSeckillSkuRelationList().stream().forEach(sku -> {
                            //生成随机码
                            String ramdomCode = UUID.randomUUID().toString().replace("-", "");
                            //创建killId
                            String killId = sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString();
                            if (Boolean.FALSE.equals(operations.hasKey(killId))) {
                                SeckillSkuIRelationDetailTO detail = new SeckillSkuIRelationDetailTO();
                                BeanUtils.copyProperties(sku, detail);
                                //匹配sku详细内容
                                if (skuInfoList != null && skuInfoList.size() > 0) {
                                    Iterator<SkuInfoVO> iterator = skuInfoList.iterator();
                                    while (iterator.hasNext()) {
                                        SkuInfoVO info = iterator.next();
                                            if (Objects.equals(info.getSkuId(), sku.getSkuId())) {
                                                detail.setSkuInfo(info);
                                                break;
                                            }
                                    }
                                }
                                //保存活动时间轴
                                detail.setStartTime(session.getStartTime().getTime());
                                detail.setEndTime(session.getEndTime().getTime());
                                //保存sku的随机码
                                detail.setRamdomCode(ramdomCode);
                                String redisString = JSON.toJSONString(detail);
                                operations.put(sku.getPromotionSessionId().toString() + "-" + sku.getSkuId().toString(), redisString);
//保存总秒杀数量，作为信号量
                                String tokenKey = SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_TOKEN_SEMAPHORE.getMsg();
                                RSemaphore semaphore = redissonClient.getSemaphore(tokenKey + ramdomCode);
                                semaphore.trySetPermits(sku.getSeckillCount());
                                //设置过期时间
                                semaphore.expireAt(session.getEndTime());
                            }
                    });
                });
            }
        }
    }

}
