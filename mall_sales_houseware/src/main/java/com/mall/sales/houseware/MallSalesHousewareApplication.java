package com.mall.sales.houseware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@EnableFeignClients(basePackages = "com.mall.sales.houseware.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSalesHousewareApplication {
    public static void main(String[] args) {

        SpringApplication.run(MallSalesHousewareApplication.class, args);
    }

}
