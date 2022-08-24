package com.mall.sales.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.product.entity.SkuInfoEntity;
import com.mall.sales.product.service.SkuInfoService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;



/**
 * sku信息
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity spuVo){

        skuInfoService.save(spuVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){

        skuInfoService.updateById(skuInfo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = skuInfoService.anotherQueryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询sku信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){

		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 查询sku的价格(存在价格变化)
     */
    @GetMapping("/priceinfo/{skuId}")
    public BigDecimal getPriceInfo(@PathVariable("skuId") Long skuId){

        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return skuInfo.getPrice();
    }

    /**
     * 按照id列表查詢
     */
    @GetMapping("/{skuIdList}")
    public List<SkuInfoEntity> getInfoByskuIdList(@RequestParam(value = "skuIdList", required = true) List<Long> skuIdList){

        return skuInfoService.getInfoByskuIdList(skuIdList);
    }

}
