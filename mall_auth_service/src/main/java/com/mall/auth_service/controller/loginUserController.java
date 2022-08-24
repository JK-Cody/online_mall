package com.mall.auth_service.controller;

import com.mall.auth_service.service.LoginUserService;
import com.mall.auth_service.vo.LoginUserLoginInVo;
import com.mall.auth_service.vo.LoginUserRegisterVo;
import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户商城登录
 */
@Controller
public class loginUserController {

    @Autowired
    LoginUserService loginUserService;

    /**
     * 发送短信验证码
     */
    @ResponseBody
    @PostMapping("/loginUser/sendSMSCode")
    public R sendSMSCode(@RequestParam("mobile") String mobile) {

        R result = loginUserService.getSMSCode(mobile);
        return R.ok(result);
    }

    /**
     * 用户注册专页register.html注册
     */
    @PostMapping("/loginUser/register")
    public String register(@Valid LoginUserRegisterVo loginUserRegisterVo,
                           BindingResult result,
                           RedirectAttributes attributes) {

       return loginUserService.userRegistrationCheck(loginUserRegisterVo,result,attributes);
    }

    /**
     * 用户登录专页login.html登录
     */
    @PostMapping("/loginUser/loginIn")
    public String loginIn(@Valid LoginUserLoginInVo loginUserLoginInVo,
                           RedirectAttributes attributes,
                           HttpSession session) {

        return loginUserService.userLoginIn(loginUserLoginInVo,attributes,session);
    }

    /**
     * 强制用户登录后，跳转到首页
     */
    @GetMapping("/login.html")
    public String loginInRedirect(HttpSession session){
        //判断是否已登录
        Object attribute = session.getAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
        if(attribute == null){
            return "login"; //返回login.html
        }
        return "redirect:http://mall.com";
    }

    /**
     * 用户点击登出后跳转到首页
     */
    @GetMapping("/loginOut.html")
    public String loginOutRedirect(HttpSession session) {
        //判断是否已登录
        Object attribute = session.getAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
        //删除登录标识
        if(attribute != null){
            session.removeAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
            return "redirect:http://mall.com";
        }
        return "redirect:http://mall.com";
    }

}