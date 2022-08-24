package com.mall.sales.houseware.dao;

import com.mall.sales.houseware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-13 01:17:23
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStockAvailable(@Param("skuId") Long skuId);

    List<Long> getSkuHousewareIds(@Param("skuId") Long skuId );

    Long getSkuStockBeingLock(@Param("skuId")Long skuId, @Param("wareId") Long wareId, @Param("number") Integer number);

    void stockUnLock(@Param("skuId")Long skuId, @Param("wareId") Long wareId, @Param("number") Integer number);

}
