package test;

import com.mall.sales.third_party_service.MallSalesOSSApplication;
import com.mall.sales.third_party_service.constant.AliSMSTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallSalesOSSApplication.class)
public class ThirdPartyServiceTest {

    @Autowired
    AliSMSTemplate aliSmsTemplate;

    @Test
    public void testMethod(){

        String result = aliSmsTemplate.sendSmsCode("19168687995", "2131");
        System.out.println(result);
    }

}
