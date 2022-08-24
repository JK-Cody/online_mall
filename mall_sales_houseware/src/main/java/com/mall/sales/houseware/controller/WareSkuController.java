package com.mall.sales.houseware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.exception.StockException;
import com.mall.sales.houseware.vo.SkuHasStock;
import com.mall.sales.houseware.vo.SkuStockLockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.houseware.entity.WareSkuEntity;
import com.mall.sales.houseware.service.WareSkuService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 商品库存
 */
@RestController
@RequestMapping("houseware/sku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.anotherQueryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 列表循环判断是否有库存
     */
    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds) {

        List<SkuHasStock> skusHasStockList = wareSkuService.getSkusHasStock(skuIds);
        return R.ok().put("skusHasStockList",skusHasStockList);
    }

    /**
     * 锁定订单的sku库存
     */
    @PostMapping("/stockLock")
    public R getSkuStockLock(@RequestBody SkuStockLockVO skuStockLockVO) {

        try {
            Boolean skuStockLock = wareSkuService.getSkuStockLock(skuStockLockVO);
            return R.ok().setData(skuStockLock);
        }catch (StockException e){
            return R.error(BusinessCodeExceptionEnum.NOT_SKU_STOCK_EXCEPTION.getCode(),BusinessCodeExceptionEnum.NOT_SKU_STOCK_EXCEPTION.getMsg());
        }
    }

}
