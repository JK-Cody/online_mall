package com.mall.sales.houseware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.houseware.dao.WareOrderTaskDao;
import com.mall.sales.houseware.entity.WareOrderTaskEntity;
import com.mall.sales.houseware.service.WareOrderTaskService;

@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据订单号查询任务的配送单
     */
    @Override
    public WareOrderTaskEntity getOrderTaskStatusByOrderSn(String orderSn) {

        return this.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn",orderSn));
    }

}