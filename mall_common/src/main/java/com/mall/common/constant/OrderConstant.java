package com.mall.common.constant;

/**
 * 订单模块
 */
public class OrderConstant {

    public static final String ORDER_COMFIRM_ORDERTOKEN_PREFIX = "comfirm:ordertoken";

    public enum AttrEnum {
        SUBMIT_ORDER_SUCCEED(0, "订单提交成功"),
        CHECK_ORDER_TOKEN_FAILURE(1, "订单令牌无效"),
        CHECK_ORDER_PAYAMOUNT_FAILURE(2, "验证订单支付总价不一致");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
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

}
