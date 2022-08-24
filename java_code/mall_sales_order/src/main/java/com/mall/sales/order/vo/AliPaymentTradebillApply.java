package com.mall.sales.order.vo;

import lombok.Data;

@Data
public class AliPaymentTradebillApply {

    public static final String trade= "trade";
    public static final String signcustomer= "signcustomer";

    private String bill_date;  //账单日期
    private String bill_type;  //账单类型, 交易账单=trade / 业务账单=signcustomer
}
