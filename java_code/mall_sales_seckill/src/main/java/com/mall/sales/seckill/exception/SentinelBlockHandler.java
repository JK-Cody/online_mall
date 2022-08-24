package com.mall.sales.seckill.exception;

import com.mall.sales.seckill.to.SeckillSkuIRelationDetailTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SentinelResource注解指定降级后的回调方法
 */
@Slf4j
@Component
public class SentinelBlockHandler {

    public static List<SeckillSkuIRelationDetailTO> seckillSkuListInComing3DaysBlockHandler() {

        log.info("getSeckillSkuListInComing3Days方法被降级");
        return null;
    }
}
