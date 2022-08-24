package com.mall.auth_service.vo;

import lombok.Data;

@Data
public class LoginUserLoginInVo {

    private String loginAcct; //登录账号,邮箱/用户名/已验证手机

    private String password;
}
