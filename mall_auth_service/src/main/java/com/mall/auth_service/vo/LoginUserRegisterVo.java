package com.mall.auth_service.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户注册信息的效验
 */
@Data
public class LoginUserRegisterVo {

    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}", message = "用户名需字母+数字，长度必须在6-20之间")
    @NotEmpty(message = "用户名必须提交")
    private String userName;

    @Length(min = 6,max = 20,message = "用户名长度必须在6-20之间")
    @NotEmpty(message = "密码必须提交")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")  //1开头，3-9第二位，其他9位任意
    private String mobile;

    @NotEmpty(message = "验证码必须填写")
    private String value;
}
