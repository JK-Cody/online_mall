package com.mall.sales.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.common.constant.ProductConstant;
import com.mall.sales.product.dao.AttrAttrgroupRelationDao;
import com.mall.sales.product.entity.AttrEntity;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.entity.ProductAttrValueEntity;
import com.mall.sales.product.service.AttrAttrgroupRelationService;
import com.mall.sales.product.service.AttrService;
import com.mall.sales.product.service.ProductAttrValueService;
import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrGroupVO;
import com.mall.sales.product.vo.AttrSimple;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.AttrGroupDao;
import com.mall.sales.product.entity.AttrGroupEntity;
import com.mall.sales.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 按前端参数集查询
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取属性分组分页列表
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params,Long catId) {

        String key = (String) params.get("key"); //key是查询功能的条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        //按条件查询
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) ->
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key)
            );
        }
        //默认全查询并分页
        if(catId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper );
            return new PageUtils(page);
        //按catId查询并分页
        }else {
            wrapper.eq("catalog_id", catId);
            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params),
                            wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 查询规格参数列表
     */
    @Override
    public List<AttrGroupVO> getAttrGroupWithAttrEntityList(Long catalogId) {
        //查询属性分组
        List<AttrGroupEntity> attrGroupEntitylist = this.list(new QueryWrapper<AttrGroupEntity>().eq("catalog_id", catalogId));
        //获取VO列表
        List<AttrGroupVO> collect = attrGroupEntitylist.stream().map(item -> {
                AttrGroupVO attrGroupVO = new AttrGroupVO();
                if(null!=item) {
                    BeanUtils.copyProperties(item, attrGroupVO);
                //保存含有的规格参数列表
                List<AttrEntity> attrs = attrService.getAttrEntityList(attrGroupVO.getAttrGroupId());
                List<AttrEntity> attrEntityList = attrs.stream().filter(
                            attr -> attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()).collect(Collectors.toList());
                attrGroupVO.setAttrs(attrEntityList);
            }
            return attrGroupVO;
        }).collect(Collectors.toList());
        return collect;
    }

}