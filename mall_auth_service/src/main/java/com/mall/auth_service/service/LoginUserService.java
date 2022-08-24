package com.mall.auth_service.service;

import com.mall.auth_service.vo.LoginUserLoginInVo;
import com.mall.auth_service.vo.LoginUserRegisterVo;
import com.mall.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

public interface LoginUserService {

    R getSMSCode(String mobile);

    String userRegistrationCheck(LoginUserRegisterVo loginUserRegisterVo, BindingResult result, RedirectAttributes redirectAttributes);

    String userLoginIn(LoginUserLoginInVo loginUserLoginInVo, RedirectAttributes attributes,HttpSession session);

    String getloginInByWeibo(String mobile,RedirectAttributes attributes, HttpSession session) throws Exception;

}
