package com.mall.sales.seckill.task;

import com.mall.common.constant.SeckillConstant;
import com.mall.sales.seckill.service.SeckillScheduleTaskService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀活动设置,需在前一日设定完成
 * 定时任务
 */
@Slf4j
@Component
public class SeckillScheduleTask {

    @Autowired
    SeckillScheduleTaskService seckillScheduleTaskService;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 最近三天的秒杀活动
     * 添加分布式锁
     */
    @Scheduled(cron = "*/120 * * * * ?")
    public void getSeckillScheduleTaskInComing3Days(){

        log.info("执行最近三天的秒杀活动");
        RLock lock = redissonClient.getLock(SeckillConstant.selection0fComingThreeDays.COMING_THREE_DAYS_LOCK.getMsg());
        try {
            //加锁
            lock.lock(5, TimeUnit.SECONDS);
            seckillScheduleTaskService.openSeckillSkuInComing3Days();
        }catch (Exception e) {
            log.error("执行异常,可能时间不符合");
        }
        finally {
            lock.unlock();
        }
    }

}
