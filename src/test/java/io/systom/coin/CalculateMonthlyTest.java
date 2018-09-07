package io.systom.coin;

import io.systom.coin.service.UserMonthInvestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * create joonwoo 2018. 9. 7.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class CalculateMonthlyTest {

    @Autowired
    private UserMonthInvestService userMonthInvestService;

    @Test
    public void calcTest() {
        userMonthInvestService.updateMonthlyCalculation("joonwoo88");
    }


    @Test
    public void monthlyTest() {
        userMonthInvestService.retrieveUserMonthInvestList("joonwoo88");
    }

    @Test
    public void aa() {
        System.out.println(0 / 1100);

    }

    @Test
    public void bb() {
        userMonthInvestService.getDailyInvest("joonwoo88");
    }
}