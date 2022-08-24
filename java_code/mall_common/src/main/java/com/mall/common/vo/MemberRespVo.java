package com.mall.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * MemberEntity的返回结果
 * 实现序列化后保存到session
 */
@Data
public class MemberRespVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;
    /**
     * 社交软件的登录Id
     */
    private String socialUid;
    /**
     * 用户信息获取的accessToken
     */
    private String accessToken;
    /**
     * 社交软件的类型(1是微博，2是微信)
     */
    private Integer socialSoftwareType;
    /**
     * accessToken的过期时间
     */
    private Long expiresIn;

}
