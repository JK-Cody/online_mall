package com.mall.sales.product.fallback;

import com.mall.common.exception.SentinelExceptionEnum;
import com.mall.common.utils.R;
import com.mall.sales.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * sentinel控制SeckillFeignService的流量控制
 */
@Slf4j
@Component
public class SentinelWithFeignFallback implements SeckillFeignService {

    /**
     * feign远程方法熔断后回调页面
     */
    @Override
    public R getSeckillSkuDetailInComing3Days(Long skuId) {

        log.info("getSeckillSkuDetailInComing3Days()被熔断");
        return R.error(SentinelExceptionEnum.REQUEST_BUSY.getCode(),SentinelExceptionEnum.REQUEST_BUSY.getMsg());
    }
}
