package com.mall.sales.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryFareVO {

    private MemberAddressVO memberAddressVO;
    private BigDecimal deliveryFare;
}
