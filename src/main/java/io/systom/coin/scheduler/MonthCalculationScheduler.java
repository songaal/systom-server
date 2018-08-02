package io.systom.coin.scheduler;

import io.systom.coin.model.PerformanceDaily;
import io.systom.coin.service.UserMonthInvestService;
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
    @Autowired
    private UserMonthInvestService userMonthInvestService;

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 6시간
    @Scheduled(initialDelay = 5 * 1000 * 60, fixedDelay= 6 * 1000 * 60 * 60)
    public void monthCalculation() {
        if (!"true".equalsIgnoreCase(isMonthCalculation)) {
            logger.debug("MonthCalculation Disabled");
            return;
        }

        logger.debug("------- 사용자별 월 수익/투자 금액 계산 진행 -------");
        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveUpdateTargetUserList");
        logger.debug("이번달 사용자 수: {}", userList.size());
        userMonthInvestService.updateMonthlyCalculation(userList);
        logger.debug("------- 사용자별 월 수익/투자 금액 계산 완료. -------");
    }


}