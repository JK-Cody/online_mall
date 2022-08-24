package com.mall.search_service.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Elasticsearch配置
 */
@SpringBootConfiguration
public class ELSearchConfig {
    //客户端共同的RequestOptions
    public static final RequestOptions COMMON_OPTIONS;

    /**
     * 构造器
     */
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 创建客户端
     */
    @Bean
    public RestHighLevelClient client() {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.179.130", 9200, "http")
                )
        );
    }
}
