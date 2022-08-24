package com.mall.sales.product.service.impl;

import com.mall.sales.product.vo.ProductAttrValueVO;
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

import com.mall.sales.product.dao.ProductAttrValueDao;
import com.mall.sales.product.entity.ProductAttrValueEntity;
import com.mall.sales.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    ProductAttrValueDao productAttrValueDao;

    /**
     * 参数查询并分页
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取参数规格列表
     */
    @Override
    public List<ProductAttrValueEntity> getbaseAttrlistforspu(Long spuId) {

        return this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    /**
     * 修改参数规格
     */
    @Transactional
    @Override
    public void updateBaseAttr(Long spuId, List<ProductAttrValueVO> entities) {
        //方式一：批量更新部分字段
        List<ProductAttrValueEntity> productAttrValueEntitylist = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        this.baseMapper.updateByEntityList(spuId,entities);
        //方式二:删除后保存
//        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
//        List<ProductAttrValueEntity> collect = parmsEntity.stream().map(item -> {
//            item.setSpuId(spuId);
//            return item;
//        }).collect(Collectors.toList());
//         this.saveBatch(collect);
    }

}