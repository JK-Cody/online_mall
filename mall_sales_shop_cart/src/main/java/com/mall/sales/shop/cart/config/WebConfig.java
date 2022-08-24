package com.mall.sales.shop.cart.config;

import com.mall.sales.shop.cart.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 调用自定义拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //对所有请求均拦截
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/**");
    }
}
