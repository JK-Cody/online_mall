package com.mall.auth_service.controller;

import com.mall.auth_service.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * 社交登录
 */
@Controller
public class LoginUserByOauth2Controller {

    @Autowired
    LoginUserService loginUserService;

    /**
     * 微博登录
     */
    @GetMapping("/weiboLogin/success")
    public String loginInByWeibo(@RequestParam("code") String code, RedirectAttributes attributes, HttpSession session) throws Exception {

        return loginUserService.getloginInByWeibo(code,attributes,session);
    }

}
