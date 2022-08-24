package com.mall.sales.order.vo;

import com.mall.sales.order.entity.OrderEntity;
import lombok.Data;

/**
 * 购物车订单的提交数据的结果
 */
@Data
public class ResponseOfCreatingOrderVO {

    private OrderEntity order;
    /** 状态码 **/
    private Integer code;  //0为成功
}
