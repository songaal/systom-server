package io.systom.coin;

import io.systom.coin.model.Invoice;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class InvoiceTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvoiceTest.class);

    @Autowired
    private SqlSession sqlSession;


    @Test
    public void calculationInvoiceTest() {

        //        대상 조회
        List<Invoice> investors = sqlSession.selectList("invoice.retrieveInvoiceTarget");

        //        중복,차액발생
        List<Invoice> invoiceList = sqlSession.selectList("invoice.findInvoiceByInvestId", investors);
        if (invoiceList != null) {
            int investorSize = investors.size();
            int invoiceSize = invoiceList.size();
            for (int i=0; i < invoiceSize; i++) {
                Invoice invoice = invoiceList.get(i);
                for (int j=0; j < investorSize; j++) {
                    Invoice tmp = investors.get(j);
                    if (invoice.getInvestId().compareTo(tmp.getInvestId()) == 0) {
                        float diff = tmp.getPaymentPrice() - invoice.getPaymentPrice();
                        if (diff == 0) {
                            investors.remove(j);
                            break;
                        }
                        if (invoice.getStatus() != null && "OK".equalsIgnoreCase(invoice.getStatus())) {
//                          TODO  차액이 발생, 결재완료되있는 상태
                            logger.info("=======================");
                            logger.info("인보이스 결재완료. 차액 발생. 투자금: {}, 수익금: {}, 발급된 인보이스 합계: {}"
                                    , tmp.getInitCash(), tmp.getReturns(), invoice.getPaymentPrice());
                            logger.info("=======================");
                        } else {
                            // 결재 전 인보이스 재발행
                            sqlSession.delete("invoice.deleteInvoice", tmp);
                        }
                        break;
                    }
                }
            }
        }

        if (investors.size() > 0) {
            int cnt = sqlSession.insert("invoice.createInvoice", investors);
            logger.debug("cnt: {}", cnt);
        }

        sqlSession.update("invoice.updateDelayStatus");
    }

}