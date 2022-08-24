package com.mall.sales.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.constant.ProductConstant;
import com.mall.sales.product.dao.AttrAttrgroupRelationDao;
import com.mall.sales.product.entity.AttrAttrgroupRelationEntity;
import com.mall.sales.product.entity.AttrGroupEntity;
import com.mall.sales.product.entity.CategoryEntity;
import com.mall.sales.product.service.AttrAttrgroupRelationService;
import com.mall.sales.product.service.AttrGroupService;
import com.mall.sales.product.service.CategoryService;
import com.mall.sales.product.vo.AttrResultVO;
import com.mall.sales.product.vo.AttrSimple;
import com.mall.sales.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.product.dao.AttrDao;
import com.mall.sales.product.entity.AttrEntity;
import com.mall.sales.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao  relationDao;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    /**
     * 保存属性对象
     */
    @Override
    public void saveAttrVO(AttrVO attrVO) {

        AttrEntity attrEntity =new AttrEntity();
        //对象数据转换到实体类
        BeanUtils.copyProperties(attrVO,attrEntity);
        //自保存
        this.save(attrEntity);
        //如果是规格参数时,处理关联表
        if(attrVO.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            Long attrGroupId = attrVO.getAttrGroupId();
            if(null==attrGroupId){
                attrGroupId=0L;   //避免没有分组Id而无法获取列表
            }
            attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }

    /**
     * 删除规格参数（单个或多个）
     */
    @Override
    public boolean removeAttrVOByIds(List<Long> attrIdsList) {

        if (CollectionUtils.isEmpty(attrIdsList)) {
            return false;
        }
        List<AttrEntity> attrEntities = this.listByIds(attrIdsList);
        if(null==attrEntities){
            return false;
        }
        for (AttrEntity attrEntity : attrEntities) {
            //如果是规格参数时,处理关联表
            if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                //删除关联表对象
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                attrAttrgroupRelationService.removeById(relationEntity);
            }else{
                //正常情况下要么都是规格参数，要么都是销售属性
                break;
            }
        }
        //自删除
        return this.removeByIds(attrIdsList);
    }

    /**
     * 按attrId查询单个属性
     */
    @Override
    public AttrResultVO getAttrResultVO(Long attrId) {
        //实体类转换
        AttrEntity attrEntity = this.getById(attrId);
        AttrResultVO attrResultVO = new AttrResultVO();
        BeanUtils.copyProperties(attrEntity, attrResultVO);
//如果是规格参数时,处理关联表
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //获取pms_attr_attrgroup_relation的关联数据
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (null != attrAttrgroupRelationEntity) {
                //保存商品的分类Id
                attrResultVO.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (null != attrGroupEntity) {
                    //保存商品的分类组名
                    attrResultVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
//保存attrResultVO数据
        Long catalogId = attrEntity.getCatalogId();
        CategoryEntity categoryEntity = categoryService.getById(catalogId);
        if(null!=categoryEntity){
            attrResultVO.setCatalogName(categoryEntity.getName());
        }
        //保存分类的上下级路径
        Long[] categoryPath = categoryService.getCategoryPath(catalogId);
        attrResultVO.setCatalogPath(categoryPath);
        return attrResultVO;
    }

    /**
     * 更新规格参数
     */
    @Override
    @Transactional
    public void updateAttrVO(AttrVO attrVO) {
        //转换实体类
        AttrEntity attrEntity= new AttrEntity();
        BeanUtils.copyProperties(attrVO,attrEntity);
 //自更新
        this.updateById(attrEntity);
//如果是规格参数时,处理关联表
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            Long attrId = attrVO.getAttrId();
            //保存分类组Id
            Long attrGroupId = attrVO.getAttrGroupId();
            attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
            attrAttrgroupRelationEntity.setAttrId(attrId);
            UpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId);
            int count = attrAttrgroupRelationService.count(wrapper);
            //不存在关联表数据时,插入
            if(count==0){
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            //存在时，更新
            }else {
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity,wrapper);
            }
        }
    }

    /**
     * 按参数查询并分页
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取规格参数列表 或 销售属性列表
     */
    @Override
    public PageUtils getAttrResultVOListPage(Map<String, Object> params, Long catalogId, String attrType) {
//按属性类型查询
        QueryWrapper<AttrEntity> wrapper =new QueryWrapper<AttrEntity>().eq
                //按attrType的值 设置为规格参数or销售属性
                ("attr_type","base".equalsIgnoreCase(attrType)? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
//按条件查询
        String key = (String) params.get("key"); //key是查询功能的条件
        if(catalogId !=0){
            wrapper.eq("catalog_id",catalogId);
        }
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) ->{
                    obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
//分页列表设置
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper);
        PageUtils attrResultVOListPage =new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
//循环获取AttrResultVO列表数据
        List<AttrResultVO> attrResultVOList = records.stream().map(attrEntity -> {
            //实体类转换数据
            AttrResultVO attrResultVO = new AttrResultVO();
            BeanUtils.copyProperties(attrEntity, attrResultVO);
            //如果是规格参数时,处理关联表
            if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                //获取关联表数据
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (null != attrAttrgroupRelationEntity) {
                    //保存分类组名
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrResultVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //保存商品分类名
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatalogId());
            if (null != categoryEntity) {
                attrResultVO.setCatalogName(categoryEntity.getName());
            }
            return attrResultVO;
        }).collect(Collectors.toList());

        attrResultVOListPage.setList(attrResultVOList);
        return attrResultVOListPage;
    }

    /**
     * 查询关联的属性列表 / AttrEntity
     */
    @Override
    public List<AttrEntity> getAttrEntityList(Long attrgroupId) {
        //查询对应的关联表列表
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        //遍历列表内的所有属性
        List<Long> attrIds = entities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        if (attrIds.size() == 0) {
            return null;
        }
        //获取属性列表
        return this.listByIds(attrIds);
    }

    /**
     * 获取属性分组没有关联的所有属性并分页
     */
    @Override
    public PageUtils getAttrEntityListWithoutRelationPage(Map<String, Object> params, Long attrgroupId) {
//查询商品分类包含的所有属性id
        AttrGroupEntity attrGroupEntity = null;
        if(attrgroupId !=0) {
            attrGroupEntity = attrGroupService.getById(attrgroupId);
        }
        List<Long> attrIdList =new ArrayList<>();
        if(null!=attrGroupEntity) {
            //获取所有属性id(自定义sql)
            Long catalogId = attrGroupEntity.getCatalogId();
            attrIdList = relationDao.getAttrIdListByCatalogId(catalogId);
        }
//查询关联包含的所有属性id
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list();
        Set<Long> attrRelationIdList = list.stream().map(
                AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toSet());
        // attrIdList.removeAll(attrRelationIdList);
//过滤没有关联到的所有属性id
        List<AttrEntity> matchAttrEntity =new ArrayList<>();
        /* 方式一 */
        if(null != attrIdList && attrIdList.size() > 0 && attrRelationIdList.size() > 0){
            List<Long> attrIdListWithoutRelation = attrIdList.stream().filter(
                    item -> !attrRelationIdList.contains(item)).collect(Collectors.toList());
            //获取没有关联到 AttrEntity列表
            List<AttrEntity> attrEntityList = this.listByIds(attrIdListWithoutRelation);
//获取匹配查询条件的 AttrEntity
            String key = (String)params.get("key");
            //接收匹配对象
            if (!StringUtils.isEmpty(key)) {
                for (AttrEntity attrEntity : attrEntityList) {
                    if(attrEntity.getAttrId().toString().equals(key)){
                        matchAttrEntity.add(attrEntity);
                    }else if(attrEntity.getAttrName().contains(key)){  //String模糊查询
                        matchAttrEntity.add(attrEntity);
                    }
                }
            }else{
                matchAttrEntity.addAll(attrEntityList);
            }
        }
//分页设置
        IPage<AttrEntity> page = new Page<AttrEntity>(1,matchAttrEntity.size());
        page.setRecords(matchAttrEntity);
        page.setTotal(matchAttrEntity.size());
        /* 方式二 */
//        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catalog_id", catalogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
//        if(attrRelationIdList.size()>0){
//            wrapper.notIn("attr_id", attrRelationIdList);
//        }
//        String key = (String) params.get("key");
//        if(!StringUtils.isEmpty(key)){
//            wrapper.and((w)->{
//                w.eq("attr_id",key).or().like("attr_name",key);
//            });
//        }
//        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
//        PageUtils pageUtils = new PageUtils(page);

        return new PageUtils(page);
    }


    /**
     * 过滤出带检索类型的属性的id列表
     */
    @Override
    public List<Long> findAttrIdListMatchSerachType(List<Long> attrIds) {

        return baseMapper.findAttrIdListMatchSerachType(attrIds);
    }
}