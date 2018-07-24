package io.systom.coin.scheduler;

import io.systom.coin.model.PerformanceDaily;
import io.systom.coin.model.UserMonthlyInvest;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${scheduler.isMonthCalculation}")
    private String isMonthCalculation;

    private ConcurrentLinkedQueue<PerformanceDaily> performanceDailyQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private SqlSession sqlSession;

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 6시간
    @Scheduled(initialDelay = 5 * 1000 * 60, fixedDelay= 6 * 1000 * 60 * 60)
    public void monthCalculation() {
//        if (!"true".equalsIgnoreCase(isMonthCalculation)) {
//            logger.debug("MonthCalculation Disabled");
//            return;
//        }

//        TODO 환율 조회 후 월수익 계산

        logger.debug("------- 사용자별 월 수익/투자 금액 계산 진행 -------");
        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveUpdateTargetUserList");
        logger.debug("이번달 사용자 수: {}", userList.size());
        int userSize = userList.size();
        for(int i=0; i<userSize; i++) {
            UserMonthlyInvest userMonthlyInvest = sqlSession.selectOne("userMonthlyInvest.getUserMonthlyInvest", userList.get(i));
            try {
                float pct = 0;
                float ret = 0;
                if (userMonthlyInvest.getMonthEquity() != 0 && userMonthlyInvest.getInitCash() != 0) {
                    pct = userMonthlyInvest.getInitCash() / userMonthlyInvest.getMonthEquity();
                    ret = userMonthlyInvest.getMonthEquity() - userMonthlyInvest.getInitCash();
                }
                userMonthlyInvest.setUserId(userList.get(i));
                userMonthlyInvest.setMonthlyReturn(ret);
                userMonthlyInvest.setMonthlyReturnPct(pct);
                logger.debug("사용자 [{}] 월투자금: {}, 월수익률: {}, 월수익금: {}", userList.get(i),
                        userMonthlyInvest.getInitCash(),
                        userMonthlyInvest.getMonthlyReturnPct(),
                        userMonthlyInvest.getMonthlyReturn());
                sqlSession.insert("userMonthlyInvest.updateMonthlyInvest", userMonthlyInvest);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        logger.debug("------- 사용자별 월 수익/투자 금액 계산 완료. -------");
    }

}