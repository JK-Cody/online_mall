package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mall.sales.product.entity.ProductAttrValueEntity;
import com.mall.sales.product.service.AttrService;
import com.mall.sales.product.service.ProductAttrValueService;
import com.mall.sales.product.vo.AttrResultVO;
import com.mall.sales.product.vo.AttrVO;
import com.mall.sales.product.vo.ProductAttrValueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 商品属性
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 保存VO类
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attrVO){

        attrService.saveAttrVO(attrVO);
        return R.ok();
    }

    /**
     * 修改VO类
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attrVO){

        attrService.updateAttrVO(attrVO);
        return R.ok();
    }

    /**
     * 修改spu管理的规格维护（参数规格）
     */
    @PostMapping("/update/{spuId}")
    // @RequiresPermissions("product:attr:update")
    public R updateBaseAttr(@PathVariable("spuId") Long spuId,
                    @RequestBody List<ProductAttrValueVO> entities){

        productAttrValueService.updateBaseAttr(spuId, entities);
        return R.ok();
    }

    /**
     * 删除VO类
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){

        attrService.removeAttrVOByIds(Arrays.asList(attrIds));
        return R.ok();
    }

    /**
     * 获取单个属性信息
     */
    @RequestMapping("/info/{attrId}")
    public R getAttrResultVO(@PathVariable("attrId") Long attrId){

        AttrResultVO attrResultVO = attrService.getAttrResultVO(attrId);
        return R.ok().put("attr", attrResultVO);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 获取属性列表并分页
     */
//   请求为("/base/list/{catalogId}") 或  ("/sale/list/{catalogId}")
    @GetMapping("/{attrType}/list/{catalogId}")
    public R getAttrListPage (@RequestParam Map<String, Object> params ,
                              @PathVariable("catalogId") Long catalogId,
                              @PathVariable("attrType") String attrType) {
            //获取规格参数列表 或 销售属性列表
            PageUtils attrListPage = attrService.getAttrResultVOListPage(params,catalogId,attrType);
            return R.ok().put("attrListPage",attrListPage);
        }

    /**
     * 获取spu管理的规格维护（参数规格列表）
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getbaseAttrlistforspu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> entities = productAttrValueService.getbaseAttrlistforspu(spuId);
        return R.ok().put("data",entities);
    }

}
