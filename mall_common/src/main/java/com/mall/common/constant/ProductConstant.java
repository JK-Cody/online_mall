package com.mall.common.constant;

public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_SALE(0,"销售属性"),
        ATTR_TYPE_BASE(1,"规格参数"),
        ATTR_SEARCH_TYPE(2,"检索类型"),
        ATTR_WITHOUT_SEARCH_TYPE(3,"非检索类型");

        private int code;
        private String msg;

        AttrEnum(int code,String msg){
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

    public enum PublishStatusEnum{

        STATUS_NEW_CREATE(0,"新建"),
        STATUS_PUBLISH(1,"spu上架"),
        STATUS_STOP(2,"spu下架");

        private int code;
        private String msg;

        PublishStatusEnum(int code,String msg){
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
