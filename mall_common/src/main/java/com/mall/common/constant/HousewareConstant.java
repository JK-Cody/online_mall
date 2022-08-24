package com.mall.common.constant;

/**
 * mall_sales_houseware模块
 */
public class HousewareConstant {
    /**
     * 采购单参数
     */
    public enum PurchaseStatusEnum{
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),
        FINISH(3,"已完成"),
        HASERROR(4,"有异常");

        private int code;
        private String msg;

        private PurchaseStatusEnum(int code,String msg){
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

    /**
     * 采购需求参数
     */
    public enum PurchaseDetailStatusEnum{
        CREATED( 0, "新建"),
        ASSIGNED( 1,"已分配"),
        BUYING( 2, "正在采购"),
        FINISH( 3, "已完成"),
        HASERROR( 4,"采购失败");

        private int code;
        private String msg;

        private PurchaseDetailStatusEnum(int code,String msg){
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

    /**
     * 订单任务的库存工作单的锁定状态
     */
    public enum WareOrderTaskDetailLockStatusEnum{
        BEINGLOCKED( 1, "已锁定 "),
        UNLOCK( 2,"已解锁"),
        TOSUBTRACT( 3, "扣减");

        private int code;
        private String msg;

        private WareOrderTaskDetailLockStatusEnum(int code,String msg){
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
