package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.service.BrandService;
import com.mall.sales.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.product.entity.CategoryBrandRelationEntity;
import com.mall.sales.product.service.CategoryBrandRelationService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:25
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;

    /**
     * 获取商品——品牌关联列表
     */
    @RequestMapping("/list")
    public R getRelationList(@RequestParam Map<String, Object> params){

        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 通过brandId查询关联列表
     */
    @GetMapping("/catalog/list")
    public R getRelationEntityList(@RequestParam("brandId") Long brandId){   //前端参数名brandId

        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = categoryBrandRelationService.getListByBrandId(brandId);
        return R.ok().put("categoryBrandRelationEntityList", categoryBrandRelationEntityList);
    }

    /**
     * 通过catId查询品牌VO
     */
    @GetMapping("/brands/list")
    public R getBrandEntityBycatId(@RequestParam("catId") Long catId){   //前端参数名brandId
        //实体类列表转为VO列表
        List<BrandEntity> BrandEntitylist = brandService.getBrandEntityBycatId(catId);
        List<BrandVO> brandVOList = brandService.transferToVOList(BrandEntitylist);
        return R.ok().put("data", brandVOList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        //设置品牌名和商品分类名
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
