package com.mall.sales.third_party_service.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.mall.common.exception.SentinelExceptionEnum;
import com.mall.common.utils.R;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sentinel配置
 */
@Component
public class MySentinelConfig implements UrlBlockHandler {

    /**
     * 设置被限流后看到的页面
     */
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {

        R r = R.error(SentinelExceptionEnum.REQUEST_BUSY.getCode(),SentinelExceptionEnum.REQUEST_BUSY.getMsg());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(r)+"servlet使用");
    }
}
