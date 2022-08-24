package com.mall.sales.product.web;

import com.mall.sales.product.service.SkuInfoService;
import com.mall.sales.product.vo.SkuInfoForItemDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

/**
 * 商品详细介绍页面
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 依据skuId查询商品详细
     * 由item.mall.com/{skuId}.html转发查询请求
     */
    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuInfoForItemDetail item = skuInfoService.getSpuShowInfoWithAsync(skuId);
        model.addAttribute("item", item);
        return "item";
    }

}