package io.systom.coin;

import io.systom.coin.model.MonthlyReturn;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
public class DateTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(DateTest.class);
    @Test
    public void timeDate(){
        new SimpleDateFormat("yyyy-mm-dd").format(null);
    }


    @Test
    public void equalsTest() {
        System.out.println("aa".equals(null));
    }


    @Test
    public void generatorMonthReturnsTest() {
        String testStart = "20170505";
        String testEnd = "20180720";

        int startYear = Integer.parseInt(testStart.substring(0, 4));
        int startMonth = Integer.parseInt(testStart.substring(4, 6));
        int startDate = Integer.parseInt(testStart.substring(6, 8));

        int endYear = Integer.parseInt(testEnd.substring(0, 4));
        int endMonth = Integer.parseInt(testEnd.substring(4, 6));
        int endDate = Integer.parseInt(testEnd.substring(6, 8));

        logger.debug("sy: {}", startYear);
        logger.debug("sm: {}", startMonth);
        logger.debug("sd: {}", startDate);

        List<MonthlyReturn> testMonthlyReturnList = new ArrayList<>();
        boolean isSameYear = true;
        for(int y=startYear; y <= endYear; y++) {
            if (y < endYear) {
                for (int m = startMonth; m <= 12; m++) {
                    testMonthlyReturnList.add(new MonthlyReturn(formatDate(y, m, null), 0));
                }
                isSameYear = false;
            } else if (y == endYear) {
                int sm = startMonth;
                if (!isSameYear) {
                    sm = 1;
                }
                for (int m = sm; m <= endMonth; m++) {
                    testMonthlyReturnList.add(new MonthlyReturn(formatDate(y, m, null), 0));
                }
            }
        }
        logger.debug("{}", testMonthlyReturnList);
    }

    public String formatDate (Integer y, Integer m, Integer d) {
        String strY = "";
        String strM = "";
        String strD = "";
        if (y != null) {
            strY = String.valueOf(y);
        }
        if (m != null) {
            strM = m < 10 ? "0" + String.valueOf(m) : String.valueOf(m);
        }
        if (d != null) {
            strD = d < 10 ? "0" + String.valueOf(d) : String.valueOf(d);
        }
        return strY + strM + strD;
    }

}