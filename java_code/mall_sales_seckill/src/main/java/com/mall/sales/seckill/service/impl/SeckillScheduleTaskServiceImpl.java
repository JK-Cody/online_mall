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
     * ?????????????????????????????????(?????????sms_seckill_session?????????)
     */
    @Override
    public void openSeckillSkuInComing3Days() {

        log.info("?????????????????????????????????");
        /* ??????????????????????????????????????????,????????????????????? sms_seckill_session */
        R result = couponFeignService.getSeckillSessionInComing3Days();
        if(result.getCode()==0) {
            List<SeckillSessionTO> seckillSessionList = result.getData("seckillSessionList", new TypeReference<List<SeckillSessionTO>>() {
            });
            //??????????????????????????????
            saveTimeRangeAndKillIdList(seckillSessionList);
            //?????????????????????sku??????
            saveSkuRelationList(seckillSessionList);
        }
    }

    /**
     * ??????????????????????????????????????????sku??????
     */
//  @SentinelResource(value = "seckillSkuListInComing3Days",blockHandlerClass = SentinelBlockHandler.class ,blockHandler = "seckillSkuListInComing3DaysBlockHandler") //sentinel???????????????
    @Override
    public List<SeckillSkuIRelationDetailTO> getSeckillSkuListInComing3Days() {
//sentinel????????????????????????
          //?????????????????????
//        try (Entry entry = SphU.entry("seckillSkuListInComing3Days")) {
//?????????????????????
            String timeString=SeckillConstant.selection0fComingThreeDays.TIME_RANGE.getMsg();
            Set<String> keys = stringRedisTemplate.keys( timeString+"*");
            //???????????????
            for (String key : keys) {
                String replace = key.replace(timeString, "");
                String[] split = replace.split("_");
                long startTime = Long.parseLong(split[0]);
                long endTime= Long.parseLong(split[1]);
                long time = new Date().getTime();
                if (time >= startTime && time <= endTime) {
//???????????????sku??????
                    //???????????????????????????
                    List<String> range = stringRedisTemplate.opsForList().range(key, 0, 100);
                    String skuListString=SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg();
                    BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(skuListString);
                    List<String> list = operations.multiGet(range);
                    if (list != null && list.size()>0) {
                        return list.stream().map(item -> {
                            //???????????????
                            SeckillSkuIRelationDetailTO detail = JSON.parseObject(item, SeckillSkuIRelationDetailTO.class);
                            //????????????????????????????????????
                            detail.setRamdomCode(null);
                            return detail;
                        }).collect(Collectors.toList());
                    }
                    break;
                }
            }
//        } catch (BlockException e) {
//            log.warn("Sentinel????????????????????????" + e.getMessage());
//        }
        return null;
    }

    /**
     * ??????????????????????????????????????????sku????????????
     */
    @Override
    public SeckillSkuIRelationDetailTO getSeckillSkuDetailInComing3Days(Long skuId) {
//???????????????sku??????
        BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg());
        Set<String> keys = operations.keys();
        if(keys!=null && keys.size()>0){
            for (String key : keys) {
//??????skuId??????sku
                //??????????????????1-1
                if (Pattern.matches("\\d-" + skuId, key)) {
                    //???????????????
                    String v = operations.get(key);
                    SeckillSkuIRelationDetailTO detail = JSON.parseObject(v, SeckillSkuIRelationDetailTO.class);
                    if (detail != null) {
//???????????????
                        //?????????????????????????????????
                        long current = System.currentTimeMillis();
                        if (detail.getStartTime() < current && detail.getEndTime() > current) {
                            return detail;
                        }
//???????????????????????????
                        detail.setRamdomCode(null);
                        return detail;
                    }
                }
            }
        }
        return null;
    }

    /**
     * ??????????????????????????????????????????
     * killId???promotionSessionId+skuId??????,key???ramdomCode
     */
    @Override
    public String skuBeingCreateOrderByKillId(String killId, String key, Integer number) {
//??????????????????
        MemberRespVo MemberRespVo = LoginUserInterceptor.threadLocal.get();
        Long memberId = MemberRespVo.getId();
//???????????????sku
        //???????????????sku??????
        BoundHashOperations<String, String, String> operations = stringRedisTemplate.boundHashOps(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg());
        //???????????????sku,???1-1
        String skuListString = operations.get(killId);
        if (!StringUtils.isEmpty(skuListString)) {
            //???????????????
            SeckillSkuIRelationDetailTO detail = JSONObject.parseObject(skuListString, SeckillSkuIRelationDetailTO.class);
//???????????????????????????
            //???????????????
            long nowTime = new Date().getTime();
            long startTime = detail.getStartTime();
            Long endTime = detail.getEndTime();
            Long ttl=endTime-startTime;
            if( nowTime>=startTime && nowTime<=endTime){
                    //??????????????????
                    if(detail.getSeckillLimit()>=number){
                        //???????????????????????????????????????
                        String redisKillId=SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_HAD_CREATE_ORDER.getMsg()+detail.getPromotionSessionId()+"_"+detail.getSkuId();
                        Boolean saveResult = stringRedisTemplate.opsForValue().setIfAbsent(redisKillId, number.toString(), ttl, TimeUnit.MILLISECONDS);//????????????
                        if(Boolean.TRUE.equals(saveResult)){
//?????????????????????(?????????)
                            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_TOKEN_SEMAPHORE.getMsg() + detail.getRamdomCode());
                            //???????????????
                            boolean result = semaphore.tryAcquire(number);
//??????????????????
                            //?????????
                            if(result) {
                                String orderSn = IdWorker.getTimeId();
                                SeckillSkuCreateOrderTO seckillSkuCreateOrderTO = new SeckillSkuCreateOrderTO();
                                seckillSkuCreateOrderTO.setMemberId(memberId);
                                seckillSkuCreateOrderTO.setOrderSn(orderSn);
                                seckillSkuCreateOrderTO.setSkuId(detail.getSkuId());
                                seckillSkuCreateOrderTO.setPromotionSessionId(detail.getPromotionSessionId());
                                seckillSkuCreateOrderTO.setSeckillPrice(detail.getSeckillPrice());
                                seckillSkuCreateOrderTO.setSkuCount(number);
                                //????????????????????????
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillSkuCreateOrderTO);
                                return orderSn;
                            }
                        }else{
                            String message =  memberId+ redisKillId;
                            log.info("?????????????????????"+message);
                            return null;
                        }
                    }
                }else {
                    log.info("??????????????????");
                    return null;
                }
            }else {
                log.info("????????????killId?????????");
                return null;
            }
        return null;
    }


//+++++++++++++++++++++++++++++
    /**
     * ?????????????????????????????????killId??????
     */
    private void saveTimeRangeAndKillIdList( List<SeckillSessionTO> seckillSessionList){

        seckillSessionList.forEach(item->{
            long startTim = item.getStartTime().getTime();
            long endTime = item.getEndTime().getTime();
            String timeRange= SeckillConstant.selection0fComingThreeDays.TIME_RANGE.getMsg()+startTim+"_"+endTime;
            if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(timeRange))){
                //??????killId??????
                List<String> killIdList = item.getSeckillSkuRelationList().stream().map(
                        //??????promotionSessionId+skuId??????killId
                        skuRelation ->skuRelation.getPromotionSessionId() + "-" +skuRelation.getSkuId().toString()
                ).collect(Collectors.toList());
                //??????????????????????????????
                stringRedisTemplate.opsForList().leftPushAll(timeRange,killIdList);
            }
        });
    }

    /**
     * ???????????????????????????sku??????
     */
    private void saveSkuRelationList( List<SeckillSessionTO> seckillSessionList) {
//???????????????????????????sku??????
        List<Long> skuIdList = new ArrayList<>();
        if (seckillSessionList != null) {
            for (SeckillSessionTO seckillSessionTO : seckillSessionList) {
                for (SeckillSkuRelationTO seckillSkuRelationTO : seckillSessionTO.getSeckillSkuRelationList()) {
                    skuIdList.add(seckillSkuRelationTO.getSkuId());
                }
            }
            if (skuIdList.size() > 0) {
                //????????????
                List<SkuInfoVO> skuInfoList = productFeignService.getInfoByskuIdList(skuIdList);
//???????????????????????????sku??????
                seckillSessionList.stream().forEach(session -> {
                        String skuKey = SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_LIST.getMsg();
                        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(skuKey);
                        session.getSeckillSkuRelationList().stream().forEach(sku -> {
                            //???????????????
                            String ramdomCode = UUID.randomUUID().toString().replace("-", "");
                            //??????killId
                            String killId = sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString();
                            if (Boolean.FALSE.equals(operations.hasKey(killId))) {
                                SeckillSkuIRelationDetailTO detail = new SeckillSkuIRelationDetailTO();
                                BeanUtils.copyProperties(sku, detail);
                                //??????sku????????????
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
                                //?????????????????????
                                detail.setStartTime(session.getStartTime().getTime());
                                detail.setEndTime(session.getEndTime().getTime());
                                //??????sku????????????
                                detail.setRamdomCode(ramdomCode);
                                String redisString = JSON.toJSONString(detail);
                                operations.put(sku.getPromotionSessionId().toString() + "-" + sku.getSkuId().toString(), redisString);
//???????????????????????????????????????
                                String tokenKey = SeckillConstant.selection0fComingThreeDays.SECKILL_SKU_RELATION_TOKEN_SEMAPHORE.getMsg();
                                RSemaphore semaphore = redissonClient.getSemaphore(tokenKey + ramdomCode);
                                semaphore.trySetPermits(sku.getSeckillCount());
                                //??????????????????
                                semaphore.expireAt(session.getEndTime());
                            }
                    });
                });
            }
        }
    }

}
