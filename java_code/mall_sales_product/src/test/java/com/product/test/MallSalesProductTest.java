package com.product.test;

import com.mall.sales.product.MallSalesProductApplication;
import com.mall.sales.product.dao.AttrAttrgroupRelationDao;
import com.mall.sales.product.dao.AttrGroupDao;
import com.mall.sales.product.dao.CategoryDao;
import com.mall.sales.product.service.AttrAttrgroupRelationService;
import com.mall.sales.product.service.SkuSaleAttrValueService;
import com.mall.sales.product.vo.AttrSimple;
import com.mall.sales.product.vo.AttrSimpleWithAttrGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallSalesProductApplication.class)
public class MallSalesProductTest {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testMethod(){
        System.out.println(categoryDao.selectPartInfo());
    }

}
