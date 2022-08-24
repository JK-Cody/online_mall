package com.mall.search_service.service;

import com.mall.common.model.SkuForEsSearchModel;

import java.io.IOException;
import java.util.List;

public interface SkuProductSaveService {

    boolean productStatusUp(List<SkuForEsSearchModel> skuEsModels) throws IOException;
}
