package com.mall.sales.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.utils.PageUtils;
import com.mall.sales.member.entity.MemberLevelEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员等级
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity getMemberDefaultLevel();
}

