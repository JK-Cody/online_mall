package com.mall.sales.shop.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * SpringSession配置
 */
@Configuration
public class LoginSessionConfig {

    /**
     * 浏览器Cookie设置
     * session作用域配置
     */
    @Bean
    public CookieSerializer cookieSerializer(){

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        //由于session默认使用模块的网址作为域地
        cookieSerializer.setDomainName("mall.com");
        cookieSerializer.setCookieName("MallSESSION");
        return cookieSerializer;
    }

    /**
     * 从redis获取session时候的序列化器
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        //json格式
        return new GenericJackson2JsonRedisSerializer();
    }

}
