package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mall.sales.product.entity.CategoryEntity;
import com.mall.sales.product.service.CategoryService;
import com.mall.common.utils.R;

/**
 * 商品三级分类
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 商品列表
     */
    @RequestMapping("/list/tree")
    public R list(){

        List<CategoryEntity> CategoryEntityList = categoryService.listWithTree();
        return R.ok().put("categoryEntityList", CategoryEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("categoryInfo", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){

		categoryService.save(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity categoryEntity){
		categoryService.updateTable(categoryEntity);
        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/batchUpdate")
    public R update(@RequestBody CategoryEntity[] categorys){
        categoryService.updateBatchById(Arrays.asList(categorys));
        return R.ok();
    }

    /**
     * 删除(可批量)
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){

        categoryService.removeListById(Arrays.asList(catIds));
        return R.ok();
    }

}
