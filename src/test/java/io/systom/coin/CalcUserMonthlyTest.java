package io.systom.coin;

import io.systom.coin.service.UserMonthInvestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class CalcUserMonthlyTest {

    @Autowired
    private UserMonthInvestService userMonthInvestService;
    @Test
    public void calcTest() {
        userMonthInvestService.updateMonthlyCalculation("joonwoo88");
    }
}
