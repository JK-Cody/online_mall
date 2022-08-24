package com.mall.sales.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.common.exception.RRException;
import com.mall.common.utils.PageUtils;
import com.mall.sales.member.entity.MemberEntity;
import com.mall.sales.member.vo.MemberLoginInTo;
import com.mall.sales.member.vo.MemberRegisterVo;
import com.mall.sales.member.vo.WeiboUserVo;

import java.util.Map;

/**
 * 会员
 *
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-12 21:07:44
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo);

    void checkMobile(String mobile) throws RRException;

    void checkUserName(String userName) throws RRException;

    MemberEntity findMemberInfoByMemberLoginInTo(MemberLoginInTo memberLoginInTo);

    MemberEntity findMemberByWeiboUserVo(WeiboUserVo weiboUserVo) throws Exception;
}

