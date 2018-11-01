package io.systom.coin.scheduler;

import io.systom.coin.service.UserMonthInvestService;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
@Component
public class MonthlyCalculateScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MonthlyCalculateScheduler.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private UserMonthInvestService userMonthInvestService;

    @Value("${scheduler.isMonthCalculation}")
    private String isMonthCalculation;

//    6 시간마다.
    @Scheduled(cron = "* * 12 * * *")
    @Transactional
    public void task() {
        if (!"true".equalsIgnoreCase(isMonthCalculation)) {
            logger.debug("MonthCalculation Disabled");
            return;
        }
        logger.debug("==== 월 투자금/수익금 정산 스케쥴러 시작 ====");
        List<String> userList = sqlSession.selectList("userMonthlyInvest.retrieveMonthlyUser");
        logger.debug("사용자 수: {}", userList.size());
        userMonthInvestService.updateMonthlyCalculation(userList);
        logger.debug("==== 월 투자금/수익금 정산 스케쥴러 종료 ====");
    }

}