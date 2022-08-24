package com.mall.sales.houseware.to;

import lombok.Data;

/**
 * 锁定的订单任务的库存信息
 */
@Data
public class OrderDetailLockedTO {

    private Long id;
    private WareOrderTaskDetailTO wareOrderTaskDetailTO;
}
