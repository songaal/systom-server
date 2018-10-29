package io.systom.coin;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMonthlyCalculationTest {
    private static Logger logger = LoggerFactory.getLogger(UserMonthlyCalculationTest.class);
    @Autowired
    private SqlSession sqlSession;

    @Test
    public void calTest() {
//        해당 월의 투자금액
//        UserMonthlyInvestSum userInvestSum = new UserMonthlyInvestSum();
//        userInvestSum.setDate("201810");
//        userInvestSum.setUserId("joonwoo88");
//        userInvestSum.setCurrency("USDT");
//        Integer initCash = sqlSession.selectOne("userMonthlyInvest.getUserInitCashSum", userInvestSum);
//        if (initCash == null) {
//            initCash = 0;
//        }
//        logger.debug("{}", initCash);

    }


    @Test
    public void test() {
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        String nowMonth = new SimpleDateFormat("YYYYMM").format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String beforeMonth = new SimpleDateFormat("YYYYMM").format(calendar.getTime());


//
//        UserMonthlyInvestSum param = new UserMonthlyInvestSum();
//        param.setUserId("joonwoo88");
//        param.setCurrency("USDT");
//        param.setDate(nowMonth);
//
//        UserMonthlyInvestSum monthInitCash = sqlSession.selectOne("userMonthlyInvest.getUserInitCashSum", param);
//
//
//
//        DependencyInjectionContainer::factory();




    }



}
