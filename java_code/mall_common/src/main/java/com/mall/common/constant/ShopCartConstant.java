package com.mall.common.constant;
/**
 * 购物车模块
 */
public class ShopCartConstant {

    public static final String TEMP_USER_COOKIE_NAME = "temp-user-key"; //临时用户的标识

    public static final int TEMP_USER_COOKIE_TIMEOUT = 60*60*24*30; //一个月的保留期

    public static final String SHOP_CART_REDIS_PREFIX = "mall:cart:"; //购物车的标识
}
