package com.mall.search_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.mall.common.model.SkuForEsSearchModel;
import com.mall.search_service.config.ELSearchConfig;
import com.mall.search_service.constant.EsConstant;
import com.mall.search_service.service.SkuProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkuProductSaveServiceImpl implements SkuProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 添加sku商品的ES索引
     */
    @Override
    public boolean productStatusUp(List<SkuForEsSearchModel> skuForEsSearchModelList) throws IOException {
//建立索引和映射关系
        //发送批量请求
        BulkRequest bulkRequest = new BulkRequest();
        //循环添加索引
        for (SkuForEsSearchModel model : skuForEsSearchModelList) {
            //保存sku商品信息到ES索引
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);  //ES索引名
            indexRequest.id(model.getSkuId().toString());
            //实体类转为Json对象保存
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
//发送响应体
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ELSearchConfig.COMMON_OPTIONS);//判断响应
        boolean ifFailures = bulk.hasFailures();  //true为有错误
        List<String> stringList = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        if(ifFailures) {
            log.error("商品上架错误:{},返回的数据:{}", stringList, bulk);
        }
        return ifFailures;
    }
}
