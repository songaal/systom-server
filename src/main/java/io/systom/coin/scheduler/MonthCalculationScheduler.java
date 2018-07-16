package io.systom.coin.scheduler;

import io.systom.coin.model.PerformanceDaily;
import io.systom.coin.model.UserMonthlyInvest;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * create joonwoo 2018. 7. 10.
 *
 */
@Component
public class MonthCalculationScheduler {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MonthCalculationScheduler.class);

    private ConcurrentLinkedQueue<PerformanceDaily> performanceDailyQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private SqlSession sqlSession;
    // 6시간
    @Scheduled(fixedDelay= 6 * 1000 * 60 * 60)
    public void monthCalculation() {
        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveUpdateTargetUserList");
        int userSize = userList.size();
        for(int i=0; i<userSize; i++) {
            UserMonthlyInvest userMonthlyInvest = sqlSession.selectOne("userMonthlyInvest.getUserMonthlyInvest", userList.get(i));
            try {
                float pct = 0;
                if (userMonthlyInvest.getMonthlyReturn() != 0 && userMonthlyInvest.getInitCash() != 0) {
                    pct = userMonthlyInvest.getInitCash() / userMonthlyInvest.getMonthlyReturn();
                }
                userMonthlyInvest.setMonthlyReturnPct(pct);
                sqlSession.insert("userMonthlyInvest.updateMonthlyInvest", userMonthlyInvest);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

//    protected void retrievePerformanceDaily() {
//        try {
//            sqlSession.selectList("performance.retrievePerformanceDaily");
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException("[FAIL] SCHEDULER Run Execute Error.");
//        }
//    }


}