package com.mall.sales.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单确认信息展示的内容
 */
public class OrderConfirmVO {

    @Getter @Setter
    /* 会员地址列表 */
    private List<MemberAddressVO> memberAddressVos;

    @Getter @Setter
    /* 所有选中的购物车内容 */
    private List<CartItemVO> items;

    @Getter @Setter
    /* 优惠券（会员积分） */
    private Integer integration;

    /* 防止订单重复提交的令牌 */
    @Getter @Setter
    private String orderToken;

    /* 订单库存 */
    @Getter @Setter
    private Map<Long,Boolean> hasStocks;

    /* 订单总价 */
    private BigDecimal priceTotal;

    /* 应付价格 */
    private BigDecimal payPrice;

    /* 订单商品总件数 */
    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (CartItemVO item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /* 订单商品总额 */
    public BigDecimal getPriceTotal() {
        BigDecimal totalNum = BigDecimal.ZERO;
        if (items != null && items.size() > 0) {
            for (CartItemVO item : items) {
                BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                totalNum = totalNum.add(itemPrice);
            }
        }
        return totalNum;
    }

    /* 订单应付价格 */
    public BigDecimal getPayPrice() {
        return getPriceTotal();
    }

}
