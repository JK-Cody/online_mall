package com.mall.sales.product;

import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession  //改变session保存到redis
@EnableFeignClients(basePackages = "com.mall.sales.product.feign")
@MapperScan("com.mall.sales.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSalesProductApplication {
    public static void main(String[] args) {

        SpringApplication.run(MallSalesProductApplication.class, args);
    }
}
