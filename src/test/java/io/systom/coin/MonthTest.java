package io.systom.coin;

import io.systom.coin.model.UserMonthlyInvest;
import io.systom.coin.service.UserMonthInvestService;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/*
 * create joonwoo 2018. 7. 16.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class MonthTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InsertTest.class);
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private UserMonthInvestService userMonthInvestService;

    @Test
    public void aaa() {
        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveMonthlyUser");
        logger.debug("사용자 수: {}", userList.size());
        userMonthInvestService.updateMonthlyCalculation(userList);
    }


    @Test
    public void monthTest() {
//        retrieveUpdateTargetUserList

        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveUpdateTargetUserList");
        logger.debug("{}", userList );
        int userSize = userList.size();
        for(int i=0; i<userSize; i++) {
            UserMonthlyInvest userMonthlyInvest = sqlSession.selectOne("userMonthlyInvest.getUserMonthlyInvest", userList.get(i));
            logger.debug("{}", userMonthlyInvest);
            sqlSession.insert("userMonthlyInvest.updateMonthlyInvest", userMonthlyInvest);
        }
    }

    @Test
    public void userMonthCalculationTest() {
        userMonthInvestService.updateMonthlyCalculation("test1");
    }

}