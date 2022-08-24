package com.mall.common.to;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户登录的基本信息
 */
@Data
public class LoginUserTO implements Serializable {

    private Long userId;
    private String userKey;
    private Boolean tempUser = false; //未登录的用户(购物车环节)
}
