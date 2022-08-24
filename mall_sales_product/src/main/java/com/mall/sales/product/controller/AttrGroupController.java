package com.mall.sales.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mall.sales.product.entity.AttrEntity;
import com.mall.sales.product.service.AttrAttrgroupRelationService;
import com.mall.sales.product.service.AttrService;
import com.mall.sales.product.service.CategoryService;
import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.product.entity.AttrGroupEntity;
import com.mall.sales.product.service.AttrGroupService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;



/**
 * 属性分组
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 批量删除关联表VO
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelationList(@RequestBody AttrAttrgroupRelationVO[] attrAttrgroupRelationVO) {

        attrAttrgroupRelationService.deleteBatchRelationVO(attrAttrgroupRelationVO);
        return R.ok();
    }

    /**
     * 列表并分页
     */
    @RequestMapping("/list/{catId}")
    public R list(@RequestParam Map<String, Object> mapParams,
                  @PathVariable("catId") Long catId){

        PageUtils page = attrGroupService.queryPage(mapParams,catId);
        return R.ok().put("page", page);
    }

    /**
     * 批量添加关联属性
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrAttrgroupRelationVO> attrAttrgroupRelationVO) {

        attrAttrgroupRelationService.saveBatchRelationVO(attrAttrgroupRelationVO);
        return R.ok();
    }

    /**
     * 获取商品分类关联的所有属性分组包含的属性列表
     */
    @GetMapping("/{catalogId}/withattr")
    public R getRelationVO(@PathVariable("catalogId") Long catalogId) {

        List<AttrGroupVO> AttrGroupVOList = attrGroupService.getAttrGroupWithAttrEntityList(catalogId);
        return R.ok().put("AttrGroupVOList", AttrGroupVOList);
    }

    /**
     * 获取实体类
     */
    @RequestMapping("/info/{attrGroupId}")
    public R getAttrGroupEntity(@PathVariable("attrGroupId") Long attrGroupId){

		AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrGroupId);
        Long catalogId = attrGroupEntity.getCatalogId();
        //接收属性分组的增加/修改 所属分类
        Long[] categoryPath = categoryService.getCategoryPath(catalogId);
        attrGroupEntity.setCatalogPath(categoryPath);
        return R.ok().put("attrGroupEntity", attrGroupEntity);
    }

    /**
     * 获取包含的属性列表
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrEntityList(@PathVariable("attrgroupId") Long attrgroupId) {

        List<AttrEntity> entities = attrService.getAttrEntityList(attrgroupId);
        return R.ok().put("data",entities);
    }

    /**
     * 获取未关联到的所有属性列表
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getAttrListWithoutRelation(@PathVariable("attrgroupId") Long attrgroupId,@RequestParam Map<String, Object> params) {

        PageUtils AttrListWithoutRelation = attrService.getAttrEntityListWithoutRelationPage(params,attrgroupId);
        return R.ok().put("AttrListWithoutRelation",AttrListWithoutRelation);
    }

}
