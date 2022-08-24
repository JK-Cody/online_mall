package com.mall.sales.seckill.interceptor;

import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 自定义用户登录和未登录的拦截器
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    //将登录信息保存为固定线程
    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

    /**
     * Controller方法处理之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//仓库模块远程调用订单模块的getOrderStatus方法时，需要登录认证
        String uri = request.getRequestURI();
        //放行特定的请求
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/getSeckillSkuBeingCreateOrder",uri);
        if(match){
//从session获取会员信息
            HttpSession session = request.getSession();
            //保存用户信息
            MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
            if(memberRespVo != null){
                threadLocal.set(memberRespVo);
                return true;
                //未登录时
            }else{
                request.setAttribute("msg", "请登录后操作");
                response.sendRedirect("http://auth.mall.com/login.html");
                return false;
            }
        }
            return true;
    }

}
