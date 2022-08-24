package com.mall.sales.houseware.vo;

import lombok.Data;

import java.util.List;

/**
 * 合并多个采购需求
 */
@Data
public class MergeMultiplePurchaseDetail {

    private Long purchaseId;  //合并后的采购单Id
    private List<Long> items;   //采购需求Id列表
}
