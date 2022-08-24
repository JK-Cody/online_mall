package com.mall.sales.houseware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mall.common.utils.R;
import com.mall.sales.houseware.feign.MemberFeignService;
import com.mall.sales.houseware.vo.MemberAddressVO;
import com.mall.sales.houseware.vo.OrderFareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.houseware.dao.WareInfoDao;
import com.mall.sales.houseware.entity.WareInfoEntity;
import com.mall.sales.houseware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 按参数查询
     */
    @Override
    public PageUtils anotherQueryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<WareInfoEntity>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id", key)
                    .or()
                    .like("name", key)
                    .or()
                    .like("address", key)
                    .or()
                    .like("areacode", key)
            ;}
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 计算订单运费
     */
    @Override
    public OrderFareVo getOrderFare(Long addrId) {

        OrderFareVo fareVo = new OrderFareVo();
        //保存会员地址
        R info = memberFeignService.info(addrId);
        if (info.getCode() == 0) {
            MemberAddressVO address = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVO>() {
            });
            fareVo.setAddress(address);
            /* 临时处理:取电话号的最后两位作为订单运费 */
            String phone = address.getPhone();
            String fare = phone.substring(phone.length() - 2);
            fareVo.setOrderFare(new BigDecimal(fare));
        }
        return fareVo;
    }

}