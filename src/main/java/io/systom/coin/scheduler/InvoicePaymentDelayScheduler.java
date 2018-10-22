package io.systom.coin.scheduler;

import io.systom.coin.model.Invoice;
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
public class InvoicePaymentDelayScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvoicePaymentDelayScheduler.class);

    @Autowired
    private SqlSession sqlSession;

    @Value("${scheduler.isPaymentDelayStatus}")
    private String isPaymentDelayStatus;

//    1 시간마다.
    @Scheduled(cron = "* * 1 * * *")
    @Transactional
    public void task() {
        if (!"true".equalsIgnoreCase(isPaymentDelayStatus)) {
            logger.debug("isInvoicePaymentDelay Disabled");
            return;
        }
        logger.debug("==== 결재 지연 상태 변경 스케쥴러 시작 ====");
//       TODO 납부 확인 기능

        int cnt = sqlSession.update("invoice.updatePaymentMembershipDelay");
        cnt += sqlSession.update("invoice.updateDelayStatus");
        logger.debug("연체 인보이스 수: {} ", cnt);

        logger.debug("==== 결재 지연 상태 변경 스케쥴러 종료 ====");
    }

}