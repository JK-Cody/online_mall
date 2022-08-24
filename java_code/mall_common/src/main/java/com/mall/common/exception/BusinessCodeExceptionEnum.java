package com.mall.common.exception;

public enum BusinessCodeExceptionEnum {

    UNKNOW_EXEPTION(10000,"系统未知异常"),

    VALID_EXCEPTION( 11000,"参数格式校验失败"),

    PRODUCT_UP_EXCEPTION( 12000,"上架sku商品异常"),

    LOGIN_SMSCODE_CREATE_EXCEPTION( 20000,"短信验证码生成频率太高"),

    LOGIN_USER_EXIST_EXCEPTION( 21000,"该用户已存在"),

    LOGIN_IN_CERTIFICATION_INVALID_EXCEPTION( 21001,"用户登录凭证错误"),

    MOBILE_REGISTER_EXIST_EXCEPTION( 21002,"该手机号码已注册"),

    LOGIN_USER_WEIBO_EXCEPTION( 22001,"微博登录凭证错误"),

    NOT_SKU_STOCK_EXCEPTION( 30000,"当前订单没有足够的sku库存");

    private int code;
    private String msg;

    BusinessCodeExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
