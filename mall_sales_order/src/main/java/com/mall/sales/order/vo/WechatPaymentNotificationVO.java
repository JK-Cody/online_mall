package com.mall.sales.order.vo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Date;

/**
 * 微信支付的结果通知所携带的参数
 * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_5.shtml
 */
@Data
public class WechatPaymentNotificationVO {

        private String transaction_id;
        private Amount amount;
        private String mchid;
        private String trade_state;
        private String bank_type;
        private List<PromotionDetail> promotion_detail;
        private Date success_time;
        private Payer payer;
        private String out_trade_no;
        private String appid;
        private String trade_state_desc;
        private String trade_type;
        private String attach;
        private SceneInfo scene_info;

    @Data
     public static class Amount {

         private int payer_total;
         private int total;
         private String currency;
         private String payer_currency;
     }

    @Data
    public static class GoodsDetail  {

        private String goods_remark;
        private int quantity;
        private int discount_amount;
        private String goods_id;
        private int unit_price;
    }

    @Data
    public static class PromotionDetail {

        private int amount;
        private int wechatpay_contribute;
        private String coupon_id;
        private String scope;
        private int merchant_contribute;
        private Date name;
        private int other_contribute;
        private String currency;
        private String stock_id;
        private List<GoodsDetail> goods_detail;
    }

    @Data
    public static class Payer  {

        private String openid;
    }

    @Data
    public static class SceneInfo  {

        private String device_id;
    }
}
