package com.mall.sales.member.dao;

import com.mall.sales.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    MemberEntity findMemberInfoByMemberLoginInTo(@Param("loginAcct") String loginAcct);
}
