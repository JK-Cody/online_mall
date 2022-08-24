package com.mall.auth_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.mall.auth_service.feign.MemberFeignService;
import com.mall.auth_service.feign.ThirdPartyServiceFeignService;
import com.mall.auth_service.service.LoginUserService;
import com.mall.auth_service.utils.IntRandom;
import com.mall.auth_service.vo.LoginUserLoginInVo;
import com.mall.auth_service.vo.LoginUserRegisterVo;
import com.mall.common.vo.MemberRespVo;
import com.mall.auth_service.vo.WeiboUserVo;
import com.mall.common.constant.AuthServiceConstant;
import com.mall.common.exception.BusinessCodeExceptionEnum;
import com.mall.common.utils.HttpUtils;
import com.mall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LoginUserServiceImpl implements LoginUserService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ThirdPartyServiceFeignService thirdPartyServiceFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 获取短信验证码
     * 验证码短信变量必须是6位或以下数字
     */
    public R getSMSCode(@RequestParam("mobile") String mobile) {
//生成短信验证码
        //确认是否已有验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + mobile);
        if(null != redisCode && redisCode.length() > 0){
            //判断系统时间标记是否小于60秒,避免高频率获取验证码
            long CuuTime = Long.parseLong(redisCode.split("_")[1]); //拆分 验证码_当前时间
            if(System.currentTimeMillis() - CuuTime < 60 * 1000){
                return R.error(BusinessCodeExceptionEnum.LOGIN_SMSCODE_CREATE_EXCEPTION.getCode(), BusinessCodeExceptionEnum.LOGIN_SMSCODE_CREATE_EXCEPTION.getMsg());
            }
        }
        //生成验证码
        String sendValue = new IntRandom().get();
        String redisSendValue = sendValue + "_" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + mobile, redisSendValue , 10, TimeUnit.MINUTES);
//发送验证码
        try {
            //TODO 开启短信发送
//          thirdPartyServiceFeignService.useSMS(mobile, sendValue);
            System.out.println("sendValue："+sendValue);
        } catch (Exception e) {
            log.warn("远程调用不知名错误");
        }
        return R.ok();
    }

    /**
     * 用户注册的验证码的JSR303校验
     */
    @Override
    public String userRegistrationCheck(@Valid LoginUserRegisterVo loginUserRegisterVo, BindingResult result, RedirectAttributes attributes) {
//保存LoginUserRegisterVo效验失败的错误集合
        Map<String, String> errors = new HashMap<>();
        if (result.hasErrors()) {
            //获取错误的效验属性
            errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            log.info("注册信息规则校验错误");
            return "redirect:http://auth.mall.com/register.html"; //重定向到注册页
        } else {
//删除验证码
            //比对验证码
            String SMSCode = stringRedisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + loginUserRegisterVo.getMobile());
            if (!StringUtils.isEmpty(SMSCode) && loginUserRegisterVo.getValue().equals(SMSCode.split("_")[0])) {
                //验证码删除
                stringRedisTemplate.delete(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + loginUserRegisterVo.getMobile());
                //调用会员服务保存注册信息
                R r = memberFeignService.memberRegistrations(loginUserRegisterVo);
                if (r.getCode() == 0) {
                    return "redirect:http://auth.mall.com/login.html";  //重定向登录页
//保存注册错误的错误集合
                } else {
                    String msg = (String) r.get("msg");
                    errors.put("msg", r.getData("msg",new TypeReference<String>(){}));
                    attributes.addFlashAttribute("errors", errors);
                    log.info("已存在的用户名或手机号码");
                    return "redirect:http://auth.mall.com/register.html";  //重定向注册页
                }
//保存验证码失败的错误集合
            } else {
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                log.info("验证码错误");
                return "redirect:http://auth.mall.com/register.html";
            }
        }
    }

    /**
     * 用户账号密码登录验证
     */
    @Override
    public String userLoginIn(LoginUserLoginInVo loginUserLoginInVo,RedirectAttributes attributes,HttpSession session) {
//保存会员对象到session
/* 保存在redis */
        //会员账号密码登录验证
        R result = memberFeignService.memberLoginIn(loginUserLoginInVo);
        if(result.getCode()==0){
            //获取会员对象
            MemberRespVo memberRespVo = result.getData(AuthServiceConstant.LOGIN_USER_SESSION_KEY, new TypeReference<MemberRespVo>() {
            });
            log.info("\n用户:[" + memberRespVo.getUsername() + "] 使用账号密码登录");
            //保存会员对象
            session.setAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY, memberRespVo);
            return "redirect:http://mall.com";
        }else{
//保存登录错误信息
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", result.getData("msg",new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/login.html";
        }
    }

    /**
     * 微博登录
     * 使用授权code来换取AccessToken
     */
    @Override
    public String getloginInByWeibo(String code,RedirectAttributes attributes, HttpSession session) throws Exception{
//发送微博登录信息
        //设置微博Api的参数
        Map<String, String> map = new HashMap<>();
        /* 需要企业注册认证 */
        map.put("client_id", "123456789");
        map.put("client_secret", "123456789");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.mall.com/weiboLogin/success");
        map.put("code", code);
        Map<String, String> headers = new HashMap<>();
        //发送请求到微博Api，换取AccessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, null, map);
        //换取成功时
        if (response.getStatusLine().getStatusCode() == 200) {
//会员微博登录验证
            //获取微博用户的信息
            String json = EntityUtils.toString(response.getEntity());
            WeiboUserVo weiboUserVo = JSON.parseObject(json, WeiboUserVo.class);
            R result = memberFeignService.loginInByWeibo(weiboUserVo);
            if (result.getCode() == 0) {
                MemberRespVo memberRespVo = result.getData(AuthServiceConstant.LOGIN_USER_SESSION_KEY, new TypeReference<MemberRespVo>() {
                });
                log.info("\n用户:[" + memberRespVo.getUsername() + "] 使用微博账号登录");
//保存会员对象到session
/* 保存在redis */
                session.setAttribute(AuthServiceConstant.LOGIN_USER_SESSION_KEY, memberRespVo);
                return "redirect:http://mall.com";
            } else {
//保存登录错误信息
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", result.getData("msg",new TypeReference<String>(){}));
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.mall.com/login.html";
            }
        //换取失败时
        }else {
            log.error("换取AccessToken失败");
            return "redirect:http://auth.mall.com/login.html";
        }
    }

}
