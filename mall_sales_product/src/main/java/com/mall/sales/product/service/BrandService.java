package com.mall.sales.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.product.entity.BrandEntity;
import com.mall.sales.product.vo.BrandVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author myself
 * @email congdingcody@gmail.com
 * @date 2022-01-12 12:21:26
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateTable(BrandEntity brand);

    List<BrandEntity> getBrandListByIds(List<Long> brandIds);

    List<BrandEntity> getBrandEntityBycatId(Long catId);

    List<BrandVO> transferToVOList(List<BrandEntity> list);
}

