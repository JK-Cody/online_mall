package com.mall.sales.houseware.controller;

import java.util.Arrays;
import java.util.Map;

import com.mall.sales.houseware.vo.OrderFareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.houseware.entity.WareInfoEntity;
import com.mall.sales.houseware.service.WareInfoService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 仓库信息
 */
@RestController
@RequestMapping("houseware/wareinfo")
public class WareInfoController {

    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = wareInfoService.anotherQueryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){

		WareInfoEntity wareInfo = wareInfoService.getById(id);
        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){

		wareInfoService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 获取订单运费
     */
    @GetMapping("/orderFare")
    public R getOrderFare(@RequestParam("addrId") Long addrId) {

        OrderFareVo orderFare = wareInfoService.getOrderFare(addrId);
        return R.ok().put("orderFare", orderFare);
    }

}
