package com.mall.sales.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 会员信息保存
 * LoginUserRegisterVo的复制版
 */
@Data
public class MemberRegisterVo {

    private String userName;

    private String password;

    private String mobile;

}
