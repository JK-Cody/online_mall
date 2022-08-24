package com.mall.sales.shop.cart.controller;

import com.mall.sales.shop.cart.service.ShopCartService;
import com.mall.sales.shop.cart.vo.CartItem;
import com.mall.sales.shop.cart.vo.ShopCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class ShopCartController {

    @Autowired
    ShopCartService shopCartService;

    /**
     * 合并购物车商品列表
     */
    @GetMapping("/cart.html")
    public String getShopCartCombination(Model model) throws ExecutionException, InterruptedException{

        ShopCart shopCart = shopCartService.getShopCartCombination();
        model.addAttribute("cart", shopCart);
        return "cartList";  //返回cartList.html
    }

    /**
     * 添加购物车商品列表
     */
    @GetMapping("/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("number") Integer number,
                              RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        //添加到购物车
        shopCartService.addCartItem(skuId, number);
        redirectAttributes.addAttribute("skuId",skuId); //保存到redis缓存
        return "redirect:http://cart.mall.com/addCartItemSuccess.html";  //转到购物车添加成功
    }

    /**
     * 购物车添加成功
     * 避免addCartItem每次刷新都增加数量
     */
    @GetMapping(value = "/addCartItemSuccess.html")
    public String addCartItemSuccess(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //从redis缓存查询添加到购物车的内容
        CartItem cartItem = shopCartService.getCartItemBySkuId(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

    /**
     * 修改购物车商品被选中的状态
     */
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer checked) {

        shopCartService.checkItem(skuId,checked);
        return "redirect:http://cart.mall.com/cart.html";
    }

    /**
     * 增加或减少购物车商品数量
     */
    @GetMapping(value = "/addOrReduceCartItem")
    public String addOrReduceCartItem(@RequestParam(value = "skuId") Long skuId,
                                      @RequestParam(value = "number") Integer number) {

        shopCartService.addOrReduceCartItem(skuId,number);
        return "redirect:http://cart.mall.com/cart.html";
    }

    /**
     * 删除购物车商品
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {

        shopCartService.deleteIdCart(skuId);
        return "redirect:http://cart.mall.com/cart.html";
    }

    /**
     * 获取购物车商品列表
     */
    @GetMapping(value = "/cartItemList")
    @ResponseBody
    public List<CartItem> getCartItemList() {

        return  shopCartService.getCartItemList();
    }
}
