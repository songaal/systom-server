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


        logger.debug("==== 인보이스 발급 스케쥴러 시작 ====");
        List<Invoice> investors = sqlSession.selectList("invoice.retrieveInvoiceTarget");
        if (investors == null) {
            logger.debug("인보이스 발급 대상 없음.");
            return;
        }


        if (investors.size() > 0) {
            int cnt = sqlSession.insert("invoice.createGoodsInvoice", investors);
            logger.debug("발급 인보이스 수: {}", cnt);
        }


        int cnt = sqlSession.update("invoice.updateDelayStatus");
        logger.debug("연체 인보이스 수: {} ", cnt);

        logger.debug("==== 인보이스 발급 스케쥴러 종료 ====");
    }

}