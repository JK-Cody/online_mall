package com.mall.sales.houseware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单运费和地址
 */
@Data
public class OrderFareVo {

    private MemberAddressVO address;   //订单地址
    private BigDecimal orderFare;  //订单运费
}
