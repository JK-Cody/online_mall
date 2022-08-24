package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.mall.sales.product.vo.SpuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.product.entity.SpuInfoEntity;
import com.mall.sales.product.service.SpuInfoService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * spu信息
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuInfoVO spuInfoVO){

        spuInfoService.saveSpuInfo(spuInfoVO);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = spuInfoService.anotherQueryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){

        SpuInfoEntity spuInfo = spuInfoService.getById(id);
        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * spu发布
     */
    @PostMapping("/{spuId}/up")
    public R spuRelease(@PathVariable("spuId") Long spuId){

        spuInfoService.getSpuRelease(spuId);
        return R.ok();
    }

    /**
     * 根据skuId获取Spu
     */
    @GetMapping("/{skuId}")
    public R getSpuInfo(@PathVariable("skuId") Long skuId){

        return R.ok().setData(spuInfoService.getSpuInfoBySkuId(skuId));
    }
}
