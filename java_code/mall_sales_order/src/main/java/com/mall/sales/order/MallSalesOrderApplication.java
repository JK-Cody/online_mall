package com.mall.sales.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRabbit
@EnableRedisHttpSession //改变session保存到redis
@EnableFeignClients
@MapperScan("com.mall.sales.order.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSalesOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSalesOrderApplication.class, args);
    }
}
