package com.mall.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加视图控制来映射到controller层,等同于@GetMapping
 */
@Configuration
public class WebSiteConfig implements WebMvcConfigurer {

    /**
     * 预设网址跳转的依据
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/register.html").setViewName("register");
//        registry.addViewController("/login.html").setViewName("login"); //改用loginUserController的loginInRedirect
    }
}
