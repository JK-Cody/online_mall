package com.mall.sales.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedissonClient配置
 */
@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() {
          Config config = new Config();
          //单机模式配置
           config.useSingleServer().setAddress("redis://192.XXX.XXX.XXX:6379");
          //集群模式配置
//           constant.useClusterServers()
//                .setScanInterval(2000)
//                .addNodeAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }
}
