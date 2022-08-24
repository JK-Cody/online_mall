package com.mall.sales.member.vo;

import lombok.Data;

/**
 * 微博的AccessToken参数
 */
@Data
public class WeiboUserVo {

    private String accessToken;
    private String remindIn ;
    private Long expiresIn;
    private String uid;
    private String isRealName ;
}
