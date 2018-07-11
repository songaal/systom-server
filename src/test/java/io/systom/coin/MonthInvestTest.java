package io.systom.coin;

import io.systom.coin.model.PerformanceDaily;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 11.
 * 
 */
public class MonthInvestTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MonthInvestTest.class);



    @Test
    public void calculationTest() {


        PerformanceDaily d1 = randomPerformance(1, -1);
        PerformanceDaily d2 = randomPerformance(1, -1);
        PerformanceDaily d3 = randomPerformance(1, -1);

        logger.debug("d1: {}", d1);
        logger.debug("d2: {}", d2);
        logger.debug("d3: {}", d3);
    }


    public PerformanceDaily randomPerformance(int id,
                                              int trends) {

        PerformanceDaily daily = new PerformanceDaily();
        daily.setId(id);
        daily.setUpdated(new Date());
        if (trends >= 0) {
            daily.setEquity((float) Math.random() * 100);
        } else {
            daily.setEquity(((float) Math.random() * 100) * -1);
        }
        return daily;
    }
}