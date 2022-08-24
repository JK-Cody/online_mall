package com.mall.auth_service.utils;

import java.util.Random;

/**
 * 短信验证码生成
 * 测试运行纯数字4到6位
 */
public class IntRandom {

    public String get(){
        //获取随机验证码（字母 +数字）
    //   String sendValue = UUID.randomUUID().toString().substring(0,5);
        //获取随机验证码（数字）
        int max=6;
        int min=4;
        int digits = new Random().nextInt(max)%(max-min+1) + min;;
        return String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, digits - 1)));
    }
}
