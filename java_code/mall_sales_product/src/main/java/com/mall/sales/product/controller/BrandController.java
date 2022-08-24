package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mall.common.group.AddGroup;
import com.mall.common.group.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.service.BrandService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;


/**
 * 品牌
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 获取品牌列表
     */
    @RequestMapping("/list")
    public R getBrandList(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 修改状态
     * @param brand
     * @return
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand) {

        brandService.updateById(brand);
        return R.ok();
    }

    /**
     *  批量获取品牌列表
     */
    @GetMapping("/brandListInfo")
    public R getBrandListByIds(@RequestParam("brandIds") List<Long> brandIds) {

        List<BrandEntity> brandList = brandService.getBrandListByIds(brandIds);
        return R.ok().put("brandList", brandList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){

		BrandEntity brand = brandService.getById(brandId);
        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(AddGroup.class)    //指定一个或多个验证组
                  @RequestBody BrandEntity brand){

		brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(UpdateGroup.class)
                        @RequestBody BrandEntity brand){

		brandService.updateTable(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){

		brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }

}
