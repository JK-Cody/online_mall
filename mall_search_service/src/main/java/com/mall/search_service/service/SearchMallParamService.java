package com.mall.search_service.service;

import com.mall.search_service.vo.SearchMallParam;
import com.mall.search_service.vo.SearchMallResult;

public interface SearchMallParamService {

    SearchMallResult search(SearchMallParam param);

}
