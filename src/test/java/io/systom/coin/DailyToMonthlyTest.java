package io.systom.coin;

import io.systom.coin.service.GoodsService;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class DailyToMonthlyTest {
    private static Logger logger = LoggerFactory.getLogger(DailyToMonthlyTest.class);

    @Autowired
    private SqlSession sqlSession;

    @Test
    public void calc() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);

        Integer investId = 3;
        Float initCash = 500.0f;
        Date investDate = calendar.getTime();

        List<Map<String, String>> monthlyReturns = sqlSession.selectList("goods.getDailyToMonthlyReturns", investId);
//        List<Map<String, String>> monthlyReturns = new ArrayList<>();
//        Map<String, String> m = new HashMap<>();
//        m.put("monthly", "201806");
//        m.put("cum_returns", "0");
//        m.put("prev_equity", "0");
//        monthlyReturns.add(m);
//        Map<String, String> m1 = new HashMap<>();
//        m1.put("monthly", "201807");
//        m1.put("cum_returns", "-10");
//        m1.put("prev_equity", "500");
//        monthlyReturns.add(m1);
//        Map<String, String> m2 = new HashMap<>();
//        m2.put("monthly", "201808");
//        m2.put("cum_returns", "-10");
//        m2.put("prev_equity", "490");
//        monthlyReturns.add(m2);
//        Map<String, String> m3 = new HashMap<>();
//        m3.put("monthly", "201809");
//        m3.put("cum_returns", "30");
//        m3.put("prev_equity", "480");
//        monthlyReturns.add(m3);
////        Map<String, String> m4 = new HashMap<>();
////        m4.put("monthly", "201810");
////        m4.put("cum_returns", "-140.18504");
////        m4.put("prev_equity", "411.97986");
////        monthlyReturns.add(m4);
////        Map<String, String> m5 = new HashMap<>();
////        m5.put("monthly", "201811");
////        m5.put("cum_returns", "0");
////        m5.put("prev_equity", "0");
////        monthlyReturns.add(m5);


        logger.debug("{}", monthlyReturns);
        float tmp = 0;
        int monthCumRetSize = monthlyReturns.size();
        for (int i=0; i < monthCumRetSize; i++) {
            String monthly = monthlyReturns.get(i).get("monthly");
            float cumReturns = Float.parseFloat(String.valueOf(monthlyReturns.get(i).get("cum_returns")));
            float prevEquity = Float.parseFloat(String.valueOf(monthlyReturns.get(i).get("prev_equity")));
            logger.debug("{}월 전월자산: {}, 이번달 수익금: {}, 이번달 수익률: {}", monthly, prevEquity, cumReturns, ((cumReturns + prevEquity) - prevEquity) / prevEquity * 100);

        }


    }

}
