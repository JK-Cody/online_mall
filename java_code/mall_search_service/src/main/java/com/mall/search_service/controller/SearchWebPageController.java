package com.mall.search_service.controller;

import com.mall.search_service.service.SearchMallParamService;
import com.mall.search_service.vo.SearchMallParam;
import com.mall.search_service.vo.SearchMallResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchWebPageController {

    @Autowired
    SearchMallParamService searchMallParamService;

    /**
     * 保存ES搜索参数的结果集
     */
    @GetMapping("/list.html")
    public String jumpToWebPage(SearchMallParam param, Model model, HttpServletRequest request){
        //保存网址查询参数
        param.setWebQueryString(request.getQueryString());
        //动态构建出查询需要的DSL语句
        SearchMallResult searchMallResult=searchMallParamService.search(param);
        model.addAttribute("searchMallResult",searchMallResult);
        return "list";  //返回页面
    }

}
