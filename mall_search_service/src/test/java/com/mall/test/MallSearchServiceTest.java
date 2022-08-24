package com.mall.test;

import com.mall.search_service.MallSearchServiceApplication;
import javafx.application.Application;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallSearchServiceApplication.class)
public class MallSearchServiceTest {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void testMethod(){

    }
}
