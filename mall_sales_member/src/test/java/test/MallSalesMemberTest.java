package test;

import com.mall.sales.member.MallSalesMemberApplication;
import com.mall.sales.member.dao.MemberDao;
import com.mall.sales.member.dao.MemberLevelDao;
import com.mall.sales.member.service.MemberLevelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallSalesMemberApplication.class)
public class MallSalesMemberTest {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Test
    public void testMethod(){
        System.out.println(memberLevelDao.getMemberDefaultLevel());
    }

}
