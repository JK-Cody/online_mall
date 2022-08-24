package com.mall.sales.member.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 其他feign模块调用时请求数据的拦截设置
 */
@Configuration
public class FeignServiceInterceptor {

    @Bean("FeignServiceInterceptor")
    public RequestInterceptor requestInterceptor() {

        return new RequestInterceptor() {
            /**
             * 请求头数据分享
             */
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //从请求容器中获取当前线程
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request != null) {
                        //将请求头数据存放到feign模块
                        String cookie = request.getHeader("Cookie");
                        requestTemplate.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
