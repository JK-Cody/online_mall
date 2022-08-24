package com.mall.search_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession //改变session保存到redis
@EnableFeignClients(basePackages = "com.mall.search_service.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSearchServiceApplication.class, args);
    }
}
