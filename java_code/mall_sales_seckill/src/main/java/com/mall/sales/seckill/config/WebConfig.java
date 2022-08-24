package com.mall.sales.seckill.config;

import com.mall.sales.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 调用自定义拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //对所有请求均拦截
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
