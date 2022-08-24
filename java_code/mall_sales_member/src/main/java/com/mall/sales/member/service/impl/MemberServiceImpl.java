package com.mall.sales.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.exception.RRException;
import com.mall.common.utils.HttpUtils;
import com.mall.sales.member.entity.MemberLevelEntity;
import com.mall.sales.member.service.MemberLevelService;
import com.mall.sales.member.vo.MemberLoginInTo;
import com.mall.sales.member.vo.MemberRegisterVo;
import com.mall.sales.member.vo.WeiboUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.Query;

import com.mall.sales.member.dao.MemberDao;
import com.mall.sales.member.entity.MemberEntity;
import com.mall.sales.member.service.MemberService;

@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 会员注册
     */
    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
//保存会员信息
        MemberEntity entity = new MemberEntity();
        //设置新会员的默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getMemberDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());
        //检查手机号、用户名是否唯一
        checkMobile(memberRegisterVo.getMobile());
        checkUserName(memberRegisterVo.getUserName());
        entity.setMobile(memberRegisterVo.getMobile());
        entity.setUsername(memberRegisterVo.getUserName());
        entity.setNickname(memberRegisterVo.getUserName());
        // 密码加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(memberRegisterVo.getPassword()));
        baseMapper.insert(entity);
    }

    /**
     * 检查会员注册电话是否已存在
     */
    @Override
    public void checkMobile(String mobile) throws RRException {

        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", mobile)) > 0){
            throw new RRException(BusinessCodeExceptionEnum.MOBILE_REGISTER_EXIST_EXCEPTION.getMsg(), BusinessCodeExceptionEnum.MOBILE_REGISTER_EXIST_EXCEPTION.getCode());
        }
    }

    /**
     * 检查会员注册用户名是否已存在
     */
    @Override
    public void checkUserName(String userName) throws RRException {

        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName)) > 0){
            throw new RRException(BusinessCodeExceptionEnum.LOGIN_USER_EXIST_EXCEPTION.getMsg(),BusinessCodeExceptionEnum.LOGIN_USER_EXIST_EXCEPTION.getCode());
        }
    }

    /**
     * 验证会员登录的账号密码
     */
    @Override
    public MemberEntity findMemberInfoByMemberLoginInTo(MemberLoginInTo memberLoginInTo) {
        //比对账号密码
        String loginAcct = memberLoginInTo.getLoginAcct();
        String password = memberLoginInTo.getPassword();
        MemberEntity memberEntity = this.baseMapper.findMemberInfoByMemberLoginInTo(loginAcct);
        if (memberEntity != null) {
            //解密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean ifMatches = bCryptPasswordEncoder.matches(password, memberEntity.getPassword());
            if (ifMatches) {
                return memberEntity;
            } else {
                log.info("密码解密错误");
                return null;
            }
        }else {
            log.info("数据库不存在该会员");
            return null;
        }
    }

    /**
     * 验证会员的微博登录
     *
     */
    @Override
    public MemberEntity findMemberByWeiboUserVo(WeiboUserVo weiboUserVo) {

        MemberEntity updateOrAdd = new MemberEntity();
        String uid = weiboUserVo.getUid();
//之前使用其它方式登录过
//保存微博登录的AccessToken参数
        //依据social_uid判断是否为首次登录
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", weiboUserVo.getUid()));
        //之前使用其它方式登录过
        if (memberEntity != null) {
            //更新AccessToken参数
            updateOrAdd.setId(memberEntity.getId());
            updateOrAdd.setAccessToken(weiboUserVo.getAccessToken());
            updateOrAdd.setExpiresIn(weiboUserVo.getExpiresIn());
            this.baseMapper.updateById(updateOrAdd);
            //不属于密码登录
            updateOrAdd.setPassword(null);
            //保存AccessToken参数到数据库
            memberEntity.setAccessToken(weiboUserVo.getAccessToken());
            memberEntity.setExpiresIn(weiboUserVo.getExpiresIn());
            return memberEntity;
//之前未登录过
//回调和保存微博注册信息
        } else {
            //官方API查询，根据用户ID获取用户信息
            HashMap<String, String> map = new HashMap<>();
            map.put("access_token", weiboUserVo.getAccessToken());
            map.put("uid", uid);
            try {
                //保存微博注册信息
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), map);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    updateOrAdd.setNickname(jsonObject.getString("nickname"));
                    updateOrAdd.setUsername(jsonObject.getString("name"));
                    updateOrAdd.setGender("m".equals(jsonObject.getString("gender")) ? 1 : 0);
                    updateOrAdd.setCity(jsonObject.getString("location"));
                    updateOrAdd.setEmail(jsonObject.getString("email"));
                }
            } catch (Exception e) {
                log.warn("****社交登录时远程调用出错****");
            }
            //保存用户状态
            updateOrAdd.setStatus(0);
            updateOrAdd.setCreateTime(new Date());
            updateOrAdd.setLevelId(1L);
            updateOrAdd.setSocialUid(uid);
            //更新AccessToken参数
            updateOrAdd.setAccessToken(weiboUserVo.getAccessToken());
            updateOrAdd.setExpiresIn(weiboUserVo.getExpiresIn());
            this.baseMapper.insert(updateOrAdd);
            //不属于密码登录
            updateOrAdd.setPassword(null);
            return updateOrAdd;
        }
    }

}