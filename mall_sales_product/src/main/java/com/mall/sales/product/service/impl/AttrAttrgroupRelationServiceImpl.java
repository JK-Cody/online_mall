package com.mall.sales.product.service.impl;

import com.mall.sales.product.vo.AttrAttrgroupRelationVO;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.AttrAttrgroupRelationDao;
import com.mall.sales.product.entity.AttrAttrgroupRelationEntity;
import com.mall.sales.product.service.AttrAttrgroupRelationService;

@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    /**
     * 批量保存VO
     */
    @Override
    public void saveBatchRelationVO(List<AttrAttrgroupRelationVO> attrAttrgroupRelationVO) {

        List<AttrAttrgroupRelationEntity> collect = attrAttrgroupRelationVO.stream().map( item -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    /**
     * 删除
     */
    @Override
    public void deleteBatchEntity(List<AttrAttrgroupRelationEntity> entities) {

        this.removeByIds(entities);
    }

    /**
     * 删除VO
     */
    @Override
    public void deleteBatchRelationVO(AttrAttrgroupRelationVO[] attrAttrgroupRelationVO) {

        List<AttrAttrgroupRelationEntity> entities = Arrays.stream(attrAttrgroupRelationVO).map((item) -> {
            //转换为实体类
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        //自定义sql
        relationDao.deleteBatchEntityByAttrIdAndAttrGroupId(entities);
    }

    /**
     * 列表并分页
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 查询组ID
     */
    @Override
    public  List<AttrSimpleWithAttrGroup> getAttrSimpleWithAttrGroup(Long spuId) {

        return this.baseMapper.getAttrSimpleWithAttrGroup(spuId);
    }
}