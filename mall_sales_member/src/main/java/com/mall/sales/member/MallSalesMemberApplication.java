package com.mall.sales.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableFeignClients
@EnableRedisHttpSession //改变session保存到redis
@MapperScan("com.mall.sales.member.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSalesMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSalesMemberApplication.class, args);
    }

}
