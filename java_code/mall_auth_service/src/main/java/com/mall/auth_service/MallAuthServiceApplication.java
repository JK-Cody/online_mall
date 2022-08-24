package com.mall.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession  //改变session保存到redis
@EnableFeignClients(basePackages = "com.mall.auth_service.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MallAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthServiceApplication.class, args);
    }
}
