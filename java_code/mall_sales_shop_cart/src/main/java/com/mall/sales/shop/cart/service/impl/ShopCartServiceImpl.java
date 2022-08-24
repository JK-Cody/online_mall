package com.mall.sales.shop.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mall.common.constant.ShopCartConstant;
import com.mall.common.to.SkuInfoTO;
import com.mall.common.utils.R;
import com.mall.sales.shop.cart.feign.ProductFeignService;
import com.mall.sales.shop.cart.interceptor.LoginUserInterceptor;
import com.mall.sales.shop.cart.service.ShopCartService;
import com.mall.sales.shop.cart.vo.CartItem;
import com.mall.common.to.LoginUserTO;
import com.mall.sales.shop.cart.vo.ShopCart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopCartServiceImpl implements ShopCartService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * 添加商品到购物车
     * 异步编排流程
     */
    @Override
    public CartItem addCartItem(Long skuId, Integer number) throws ExecutionException, InterruptedException {
//查询是否有保存购物车
        BoundHashOperations<String, Object, Object> boundHashOperations = this.saveLoginUserWithCartKey();
        String productRedisValue = (String) boundHashOperations.get(skuId.toString());
        //没有时，异步保存sku信息
        if (StringUtils.isEmpty(productRedisValue)) {
            //查询sku商品对象
            R result = productFeignService.info(skuId);
            //异步保存到购物车
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                SkuInfoTO skuInfo = result.getData("skuInfo", new TypeReference<SkuInfoTO>() {
                });
                cartItem.setSkuId(skuInfo.getSkuId());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setCount(number);
            },executor);
            CompletableFuture<Void> getSkuInfoFutureForAttrValuelist = CompletableFuture.runAsync(() -> {
                List<String> saleAttrValuelist = productFeignService.getSaleAttrValuelist(skuId);
                cartItem.setSkuAttrList(saleAttrValuelist);
            },executor);
            //等待全部完成的结果
            CompletableFuture.allOf(getSkuInfoFuture, getSkuInfoFutureForAttrValuelist).get();
//保存到redis
            //转换购物车对象
            String cartItemJson = JSON.toJSONString(cartItem);
            boundHashOperations.put(skuId.toString(), cartItemJson);
            return cartItem;
        //存在时，保存数量
        }else {
            CartItem cartItem = JSON.parseObject(productRedisValue, CartItem.class);
            //修改数量
            cartItem.setCount(cartItem.getCount() + number);
            //转换购物车对象
            String cartItemJson = JSON.toJSONString(cartItem);
            boundHashOperations.put(skuId.toString(),cartItemJson);
            return cartItem;
        }
    }

    /**
     * 合并购物车商品列表，包括临时购物车和登录用户购物车
     */
    @Override
    public ShopCart getShopCartCombination() throws ExecutionException, InterruptedException {
//获取用户信息
        LoginUserTO loginUserTO = LoginUserInterceptor.threadLocal.get();
//获取用户购物车信息
        ShopCart shopCart = new ShopCart();
        //登录时
        if (loginUserTO.getUserId() != null) {
            //查询是否有临时用户购物车
            String tempUserCartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX + loginUserTO.getUserKey();
            List<CartItem> tempCartItems = getCartByCartKey(tempUserCartKey);
            if (tempCartItems != null) {
                //合并临时购物车
                for (CartItem item : tempCartItems) {
                    addCartItem(item.getSkuId(),item.getCount());
                }
                //清除临时购物车的数据
                clearCart(tempUserCartKey);
            }
            //查询登录用户购物车
            String LoginUserCartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX + loginUserTO.getUserId();
            List<CartItem> cartItems = getCartByCartKey(LoginUserCartKey);
            shopCart.setCartItemList(cartItems);
        } else {
            //没登录时
            String cartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX + loginUserTO.getUserKey();
            //获取临时用户购物车内容
            List<CartItem> cartItems = getCartByCartKey(cartKey);
            shopCart.setCartItemList(cartItems);
        }
        return shopCart;
    }

    /**
     * 根据用户标识清除购物车
     */
    @Override
    public void clearCart(String cartKey) {

        stringRedisTemplate.delete(cartKey);
    }

    /**
     * 根据skuId获取购物车内容
     */
    @Override
    public CartItem getCartItemBySkuId(Long skuId) {
        //查询redis缓存
        BoundHashOperations<String,Object,Object> boundHashOperations =this.saveLoginUserWithCartKey();
        String cartItemRedisValue = (String) boundHashOperations.get(skuId.toString());
        return JSON.parseObject(cartItemRedisValue, CartItem.class);
    }

    /**
     * 修改购物车商品被选中的状态
     */
    @Override
    public void checkItem(Long skuId, Integer checked) {
        //获取购物车内容
        CartItem cartItemBySkuId = getCartItemBySkuId(skuId);
        cartItemBySkuId.setCheck(checked == 1);
        String s = JSON.toJSONString(cartItemBySkuId);
        //获取用户标识，并重新保存商品内容到redis
        this.saveLoginUserWithCartKey().put(skuId.toString(),s);
    }

    /**
     * 增加或减少购物车商品数量
     */
    @Override
    public void addOrReduceCartItem(Long skuId, Integer number) {
        //获取购物车内容
        CartItem cartItemBySkuId = getCartItemBySkuId(skuId);
        cartItemBySkuId.setCount(number);
        String s = JSON.toJSONString(cartItemBySkuId);
        //获取用户标识，并重新保存商品内容到redis
        this.saveLoginUserWithCartKey().put(skuId.toString(),s);
    }

    /**
     * 删除购物车商品
     */
    @Override
    public void deleteIdCart(Integer skuId) {

        this.saveLoginUserWithCartKey().delete(skuId.toString());
    }

    /**
     * 获取购物车商品列表
     */
    @Override
    public List<CartItem> getCartItemList() {
//获取用户信息
        LoginUserTO loginUserTO = LoginUserInterceptor.threadLocal.get();
//根据用户标识获取购物车内容
        Long userId = loginUserTO.getUserId();
        if(userId==null){
            return null;
        }else {
            String LoginUserCartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX +userId;
            List<CartItem> cartItemList = this.getCartByCartKey(LoginUserCartKey);
            //查询sku的价格(存在价格变化)
            List<CartItem> collect = cartItemList.stream()
                    .filter(item -> item.getCheck())
                    .map(item -> {
                        BigDecimal priceInfo = productFeignService.getPriceInfo(item.getSkuId());
                        item.setPrice(priceInfo);
                        return item;
                    })
                    .collect(Collectors.toList());
            return collect;
        }
    }


//+++++++++++++++++++++++++++++++
    /**
     * 添加登录用户购物车和临时用户购物车的标识
     * 将购物车商品信息保存到redis
     */
    private BoundHashOperations<String, Object, Object> saveLoginUserWithCartKey(){
        //获取用户信息
        LoginUserTO loginUserTO = LoginUserInterceptor.threadLocal.get();
        //添加用户标识,保存到redis缓存
        Long userId = loginUserTO.getUserId();
        String cartKey ="";
        if(userId!=null){
            //给登录用户做标识,如 mall:cart:3
           cartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX+userId;
        }else{
            //给临时用户做标识 ,如 mall:cart:8649326b-74da-4543-9532-13be7d6dc542
           cartKey = ShopCartConstant.SHOP_CART_REDIS_PREFIX+ loginUserTO.getUserKey();
        }
        /*绑定一个redis键值对,用于后续调用 */
        return stringRedisTemplate.boundHashOps(cartKey);
    }

    /**
     * 根据用户标识获取购物车内容
     * 查询redis缓存
     */
    private List<CartItem> getCartByCartKey(String cartKey) {
        //获取redis缓存的购物车内容
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        //循环获取键值对的值
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItem> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;
    }


}
