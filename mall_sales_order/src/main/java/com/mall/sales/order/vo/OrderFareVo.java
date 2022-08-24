package com.mall.sales.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单运费和会员地址
 */
@Data
public class OrderFareVo {

    private MemberAddressVO address;
    private BigDecimal orderFare;  //订单运费
}
