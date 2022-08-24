package com.mall.sales.shop.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车对象
 */
//@ToString
public class ShopCart {

    private List<CartItem> cartItemList; //商品列表
    private BigDecimal reduce = new BigDecimal("0.00");  //价格减免

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    //计算购物车商品总数量
    public Integer getCountNumber() {
        int count =0;
        if(cartItemList != null && cartItemList.size()!=0){
            for (CartItem cartItem : cartItemList) {
                count+=cartItem.getCount();
            }
        }
        return count;
    }
    //计算多少个商品类型
    public Integer getCountType() {
        int countType =0;
        if(cartItemList != null && cartItemList.size()!=0){
                countType+=cartItemList.size();
        }
        return countType;
    }
    //计算支付总价
    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal(0);
        if(cartItemList != null && cartItemList.size()!=0){
            for (CartItem cartItem : cartItemList) {
                if (cartItem.getCheck()) {  //被选中
                    totalPrice.add(cartItem.getTotalPrice());
                }
            }
        }
        // 计算优惠后的价格
        totalPrice.subtract(this.getReduce());
        return totalPrice;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

}
