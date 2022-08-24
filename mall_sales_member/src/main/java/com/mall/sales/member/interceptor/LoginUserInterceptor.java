package com.mall.sales.member.interceptor;

import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

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
//放行特定的请求
//仓库模块远程调用订单模块的getOrderStatus方法时，需要登录认证
        //符合时
        String uri = request.getRequestURI();
        /* 需放行，否则mall_auth_service不通过 */
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if(match){
            return true;
        }
//从session获取会员信息
        HttpSession session = request.getSession();
        //保存用户登录信息
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY);
        if(memberRespVo != null){
            threadLocal.set(memberRespVo);
            return true;
        //未登录时跳转到登录界面
        }else{
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            request.setAttribute("msg", "请登录后操作");
            response.sendRedirect("http://auth.mall.com/login.html");
            return false;
        }
    }

}
