package com.mall.sales.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.mall.sales.coupon.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MallSalesCouponApplication {
    public static void main(String[] args) {

        SpringApplication.run(MallSalesCouponApplication.class, args);
    }

}
