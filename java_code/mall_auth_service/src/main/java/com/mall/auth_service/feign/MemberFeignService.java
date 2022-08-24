package com.mall.auth_service.feign;

import com.mall.auth_service.vo.LoginUserLoginInVo;
import com.mall.auth_service.vo.LoginUserRegisterVo;
import com.mall.auth_service.vo.WeiboUserVo;
import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-sales-member")
public interface MemberFeignService {

    @PostMapping(value = "/member/member/register")
    R memberRegistrations(@RequestBody LoginUserRegisterVo loginUserRegisterVo);

    @PostMapping(value = "/member/member/loginIn")
    R memberLoginIn(@RequestBody LoginUserLoginInVo loginUserLoginInVo);

    @PostMapping(value = "/member/member/weibo/loginIn")
    R loginInByWeibo(@RequestBody WeiboUserVo weiboUserVo) throws Exception;

    @PostMapping(value = "/member/member/wechat/loginIn")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}
