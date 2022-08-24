package com.mall.common.constant;

/**
 * 秒杀模块
 */
public class SeckillConstant {

    //redis缓存的前缀
    public static final String SECKILL_SESSION_CASHE_PREFIX = "seckill:session:";

    //最近三天的秒杀活动的选项
    public enum selection0fComingThreeDays {
        //标识
        INDEX_COMING_THREE_DAYS(10301, "coming:three:days:"),

        //时间轴
        // seckill:session:coming:three:days:
        TIME_RANGE(10302, SECKILL_SESSION_CASHE_PREFIX+INDEX_COMING_THREE_DAYS.getMsg()),

        //所有的活动时间的分布式锁
        // coming:three:days:lock:
        COMING_THREE_DAYS_LOCK(10303, INDEX_COMING_THREE_DAYS.getMsg()+"lock:"),

        //sku内容
        // coming:three:days:seckill:skulist:
        SECKILL_SKU_RELATION_LIST(10305, INDEX_COMING_THREE_DAYS.getMsg()+"seckill:skulist:"),
        // coming:three:days:seckill:skuinfo:seaphore:
        SECKILL_SKU_RELATION_TOKEN_SEMAPHORE(10306, INDEX_COMING_THREE_DAYS.getMsg()+"seckill:skuinfo:seaphore:"),

        //秒杀活动的该sku已经被创建
        SECKILL_SKU_HAD_CREATE_ORDER(10305, INDEX_COMING_THREE_DAYS.getMsg()+"seckill:sku:created:");

        private int code;
        private String msg;

        selection0fComingThreeDays(int code, String msg) {
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
