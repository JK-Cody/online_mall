package com.mall.auth_service.vo;

import lombok.Data;

/**
 * 微博的AccessToken
 */
@Data
public class WeiboUserVo {

    private String access_token;
    private String remind_in ;
    private Long expires_in;
    private String uid;
    private String isRealName ;
}
