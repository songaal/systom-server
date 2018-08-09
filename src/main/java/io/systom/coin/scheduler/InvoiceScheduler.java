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
public class InvoiceScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvoiceScheduler.class);

    @Autowired
    private SqlSession sqlSession;

    @Value("${scheduler.isInvoiceCreate}")
    private String isInvoiceCreate;

// kst 14h, utc 5h
    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void task() {
        if (!"true".equalsIgnoreCase(isInvoiceCreate)) {
            logger.debug("isInvoiceCreate Disabled");
            return;
        }

        logger.debug("==== 인보이스 발급 스케쥴러 시작 ====");
        List<Invoice> investors = sqlSession.selectList("invoice.retrieveInvoiceTarget");
        if (investors == null) {
            logger.debug("인보이스 발급 대상 없음.");
            return;
        }
        logger.debug("인보이스 발급 대상 수: {}", investors.size());
        investors = duplicateFilter(investors);

        if (investors.size() > 0) {
            int cnt = sqlSession.insert("invoice.createInvoice", investors);
            logger.debug("발급 인보이스 수: {}", cnt);
        }


        int cnt = sqlSession.update("invoice.updateDelayStatus");
        logger.debug("연체 인보이스 수: {} ", cnt);

        logger.debug("==== 인보이스 발급 스케쥴러 종료 ====");
    }

    public List<Invoice> duplicateFilter(List<Invoice> investors) {
        List<Invoice> invoiceList = sqlSession.selectList("invoice.findInvoiceByInvestId", investors);
        if (invoiceList == null) {
            return investors;
        }
        logger.debug("대상 중 이미 발급된 인보이스 수: {}", invoiceList.size());

        int investorSize = investors.size();
        int invoiceSize = invoiceList.size();
        for (int i=0; i < invoiceSize; i++) {
            Invoice invoice = invoiceList.get(i);
            for (int j=0; j < investorSize; j++) {
                Invoice tmp = investors.get(j);
                if (invoice.getInvestId().compareTo(tmp.getInvestId()) != 0) {
                    continue;
                }
                float diff = tmp.getPaymentPrice() - invoice.getPaymentPrice();
                if (diff == 0) {
                    investors.remove(j);
                    break;
                }
                logger.debug("=======================");
                logger.debug("차액 발생. 투자번호: {} 투자금: {}, 수익금: {}, 이미 발급된 인보이스 합계: {}, 발급일: {}",
                        tmp.getInvestId(), tmp.getInitCash(), tmp.getReturns(), invoice.getPaymentPrice(), invoice.getCreateTime());
                logger.debug("=======================");

                if (invoice.getStatus() != null && "OK".equalsIgnoreCase(invoice.getStatus())) {
//                          TODO  차액이 발생, 결재완료되있는 상태
                    logger.error("차액 발생. 이미 납부완료 상태... 투자번호: {} 납부금액: {} 발급 납부 금액: {}",
                            tmp.getInvestId(), tmp.getPaymentPrice(), invoice.getPaymentPrice());
                } else {
                    logger.debug("차액 발생. 미납부 인보이스 삭제 처리.");
                    sqlSession.delete("invoice.deleteInvoice", tmp);
                }
                break;
            }
        }
        return investors;
    }

}