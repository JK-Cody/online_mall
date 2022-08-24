package com.mall.sales.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.exception.RRException;
import com.mall.sales.member.vo.MemberLoginInTo;
import com.mall.sales.member.vo.MemberRegisterVo;
import com.mall.sales.member.vo.WeiboUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mall.sales.member.entity.MemberEntity;
import com.mall.sales.member.service.MemberService;
import com.mall.common.utils.PageUtils;
import com.mall.common.utils.R;

/**
 * 会员
 */
@RestController
@RequestMapping("member/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 会员注册验证
     */
    @PostMapping("/register")
    public R memberRegistrations(@RequestBody MemberRegisterVo memberRegisterVo) {
        //验证
        try {
            memberService.register(memberRegisterVo);
        //使用自定义异常预设错误的输出信息
        } catch (RRException rrException) {
            int LOGIN_USER_EXIST_EXCEPTIO = BusinessCodeExceptionEnum.LOGIN_USER_EXIST_EXCEPTION.getCode();
            int MOBILE_REGISTER_EXIST_EXCEPTION = BusinessCodeExceptionEnum.MOBILE_REGISTER_EXIST_EXCEPTION.getCode();
            if(rrException.getCode()==LOGIN_USER_EXIST_EXCEPTIO) {
                //用户已存在时
               return R.error(LOGIN_USER_EXIST_EXCEPTIO, BusinessCodeExceptionEnum.LOGIN_USER_EXIST_EXCEPTION.getMsg());
            }else if(rrException.getCode()==MOBILE_REGISTER_EXIST_EXCEPTION){
                //手机已经注册时
            return R.error(MOBILE_REGISTER_EXIST_EXCEPTION, BusinessCodeExceptionEnum.MOBILE_REGISTER_EXIST_EXCEPTION.getMsg());
          }
        }
        return R.ok();
    }

    /**
     * 会员账号密码登录验证
     */
    @PostMapping("/loginIn")
    public R memberLoginIn(@RequestBody MemberLoginInTo memberLoginInTo) {
        //获取会员
        MemberEntity User = memberService.findMemberInfoByMemberLoginInTo(memberLoginInTo);
        if(User!=null){
            return R.ok().put(AuthServiceConstant.LOGIN_USER_SESSION_KEY, User);
        }
        return R.error(BusinessCodeExceptionEnum.LOGIN_IN_CERTIFICATION_INVALID_EXCEPTION.getCode(),BusinessCodeExceptionEnum.LOGIN_IN_CERTIFICATION_INVALID_EXCEPTION.getMsg());
    }

    /**
     *  会员微博登录验证
     */
    @PostMapping("/weibo/loginIn")
    public R loginInByWeibo(@RequestBody WeiboUserVo weiboUserVo) throws Exception {
        //获取会员
        MemberEntity WeiboUser = memberService.findMemberByWeiboUserVo(weiboUserVo);
        if (WeiboUser != null) {
            return R.ok().put(AuthServiceConstant.LOGIN_USER_SESSION_KEY, WeiboUser);
        } else {
            return R.error(BusinessCodeExceptionEnum.LOGIN_USER_WEIBO_EXCEPTION.getCode(),BusinessCodeExceptionEnum.LOGIN_USER_WEIBO_EXCEPTION.getMsg());
        }
    }

    /**
     *  会员微信登录验证
     */
    @PostMapping("/wechat/loginIn")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo){

        return null;
    }

}
