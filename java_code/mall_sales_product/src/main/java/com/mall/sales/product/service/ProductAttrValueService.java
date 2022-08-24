package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.ProductAttrValueEntity;
import com.mall.sales.product.vo.ProductAttrValueVO;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> getbaseAttrlistforspu(Long spuId);

    void updateBaseAttr(Long spuId, List<ProductAttrValueVO> entities);

}

