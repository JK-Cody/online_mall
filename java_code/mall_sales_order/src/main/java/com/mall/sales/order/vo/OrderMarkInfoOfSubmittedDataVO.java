package com.mall.sales.order.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车订单的提交数据的标识内容
 */
@Data
public class OrderMarkInfoOfSubmittedDataVO {
    /* 收获地址的id */
    private Long addrId;
    /* 支付方式【 1->支付宝；2->微信；3->银联； 4->货到付款；】 */
    private Integer payType;
    /* 防重令牌 */
    private String orderToken;
    /* 应付总价 */
    private BigDecimal payPrice;
    /* 订单备注 */
    private String orderRemark;
}
