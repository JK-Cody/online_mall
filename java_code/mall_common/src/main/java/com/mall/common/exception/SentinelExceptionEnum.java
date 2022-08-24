package com.mall.common.exception;

public enum SentinelExceptionEnum {

    REQUEST_BUSY(101,"请求频率过高");

    private int code;
    private String msg;

    SentinelExceptionEnum(int code, String msg) {
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
