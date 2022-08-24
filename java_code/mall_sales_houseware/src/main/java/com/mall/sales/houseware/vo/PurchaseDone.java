package com.mall.sales.houseware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 采购单传输对象
 */
@Data
public class PurchaseDone {

    @NotNull
    private Long id; //采购单id
    private List<PurchaseItemDone> items;  //采购需求列表
}

