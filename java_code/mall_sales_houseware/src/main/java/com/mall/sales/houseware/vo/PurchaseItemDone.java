package com.mall.sales.houseware.vo;

import lombok.Data;

/**
 * 采购需求传输对象
 */
@Data
public class PurchaseItemDone {

    private Long itemId;  //采购需求
    private Integer status;
    private String reason;  //错误理由
}
