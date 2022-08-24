package com.mall.sales.third_party_service.controller;

import com.mall.common.utils.R;
import com.mall.sales.third_party_service.constant.AliSMSTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信服务
 */
@RestController
@RequestMapping("/sms")
public class SMSController {

    @Autowired
    AliSMSTemplate aliSmsTemplate;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send")
    public R useSMS(@RequestParam("mobile") String mobile,
                        @RequestParam("value") String value){  //接收方手机号,动态验证码

        String result = aliSmsTemplate.sendSmsCode(mobile, value);
        return R.ok(result);
    }
}
