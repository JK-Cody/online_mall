package com.mall.sales.shop.cart.service;

import com.mall.sales.shop.cart.vo.CartItem;
import com.mall.sales.shop.cart.vo.ShopCart;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ShopCartService {

    CartItem addCartItem(Long skuId, Integer number) throws ExecutionException, InterruptedException;

    ShopCart getShopCartCombination() throws ExecutionException, InterruptedException ;

    void clearCart(String cartKey);

    CartItem getCartItemBySkuId(Long skuId);

    void checkItem(Long skuId, Integer checked);

    void addOrReduceCartItem(Long skuId, Integer number);

    void deleteIdCart(Integer skuId);

    List<CartItem> getCartItemList();
}
