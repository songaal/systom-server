package io.systom.coin;

import io.systom.coin.model.UserMonthlyInvest;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * create joonwoo 2018. 7. 11.
 * 
 */
public class MonthInvestTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MonthInvestTest.class);



    @Test
    public void calculationTest() {
//        int monthSize = tmpMonthInvest.size();
        String lastDate = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
        lastDate = "201802";
        int lastYear = Integer.parseInt(lastDate.substring(0, 4));
        int lastMonth = Integer.parseInt(lastDate.substring(5, 6));
        boolean isChangeYear = false;
        List<UserMonthlyInvest> monthlyInvestList = new ArrayList<>();
        for (int y = lastYear; y >= lastYear - 1; y--) {
            if (isChangeYear) {
                lastMonth = 12;
            }
            for (int m = lastMonth; m >= 1; m--) {
                logger.debug("{}{}", y, m);
                monthlyInvestList.add(new UserMonthlyInvest());
                if (monthlyInvestList.size() == 6) {
                    break;
                }
            }
            if (monthlyInvestList.size() == 6) {
                break;
            } else {
                isChangeYear = true;
            }
        }
    }

}