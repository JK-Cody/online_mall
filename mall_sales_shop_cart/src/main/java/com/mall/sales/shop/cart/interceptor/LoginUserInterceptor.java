package com.mall.sales.shop.cart.interceptor;

import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.constant.ShopCartConstant;
import com.mall.common.vo.MemberRespVo;
import com.mall.common.to.LoginUserTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 自定义用户登录和未登录的拦截器
 */
public class LoginUserInterceptor implements HandlerInterceptor {
    //将登录信息保存为固定线程
    public static ThreadLocal<LoginUserTO> threadLocal = new ThreadLocal<>();

    /**
     * Controller方法处理之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
//从session获取会员信息
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
        //保存用户信息
        LoginUserTO loginUserTO = new LoginUserTO();
        if (memberRespVo != null) {
            loginUserTO.setUserId(memberRespVo.getId());
        }
//非首次使用购物车情况下
        //从cookie获取临时用户
        /* 临时用户可以添加购物车，当转为登录状态时，购物车将转移 */
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //确认user-key为临时用户
                String name = cookie.getName();
                if (name.equals(ShopCartConstant.TEMP_USER_COOKIE_NAME)) {
                    //添加临时用户标识
                    loginUserTO.setUserKey(cookie.getValue());
                    loginUserTO.setTempUser(true);
                }
            }
        }
//首次使用购物车情况下
        // 添加临时用户标识
        if (StringUtils.isEmpty(loginUserTO.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            loginUserTO.setUserKey(uuid);
        }
//同线程保存用户信息
        threadLocal.set(loginUserTO);
        return true;
    }

    /**
     * Controller方法处理之后
     * 对临时用户(购物车环节)进行cookie域设置
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //同线程获取
        LoginUserTO loginUserTO = threadLocal.get();
        //临时用户时
        if(!loginUserTO.getTempUser()) {
            //设置cookie域
            Cookie cookie = new Cookie(ShopCartConstant.TEMP_USER_COOKIE_NAME, loginUserTO.getUserKey());
            cookie.setDomain("mall.com");
            cookie.setMaxAge(ShopCartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }

}
