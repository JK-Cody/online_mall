package com.mall.sales.order.vo;

import lombok.*;

@Data
public class WechatPaymentOfTradebillOrFundflowbillApplyVO {

    public static final String bill_type= "bill_type";
    public static final String account_type= "account_type";

    private String bill_date;  //账单日期
    private String tar_type;  //压缩类型
    private String type;  //账单类型, 交易账单=bill_type / 资金账单=account_type

}
