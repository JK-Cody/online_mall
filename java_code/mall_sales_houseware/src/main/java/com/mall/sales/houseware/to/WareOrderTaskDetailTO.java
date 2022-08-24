package com.mall.sales.houseware.to;

import lombok.Data;

/**
 * WareOrderTaskDetailEntity的TO
 */
@Data
public class WareOrderTaskDetailTO {

    private Long id;
    private Long skuId;
    private String skuName;
    private Integer skuNum;
    private Long taskId;
    private Long wareId;
    private Integer lockStatus;
}
