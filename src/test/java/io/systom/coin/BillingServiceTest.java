package io.systom.coin;

import io.systom.coin.exception.BillingException;
import io.systom.coin.model.Card;
import io.systom.coin.service.BillingService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;

public class BillingServiceTest {

    private static Logger logger = LoggerFactory.getLogger(BillingServiceTest.class);

    private BillingService service;

    @Before
    public void init() {
        service = new BillingService("7173788573360317", "uuvKahDJzr2VEnIp3acBpwGzJ0tufZTJuotFF7pfzradPTDT4Pvtmb25cwzrwsa0REFxL4MkwXymQbfU");
    }

    @Test
    public void testReset() throws BillingException {
        String customerUid = "swsong_3497";
        Map result = service.unregisterCard(customerUid);
        logger.debug("unregistercard >> {}", result);
    }

    @Test
    public void testWhole() throws BillingException {
        String token = service.getToken();
        logger.debug("Token[{}]", token);
        logger.debug("-----1. 카드등록------------------------------");
        Card card = new Card();
        card.setUserId("swsong");
        card.setCardNo("5389200013543497");
        card.setYear("2021");
        card.setMonth("05");
        card.setPassword2("58");
        card.setBirthDate("781231");

        Map result = service.registerCard(card);
        Map response = (Map) result.get("response");
        String customerUid = (String) response.get("customer_uid");
        logger.debug("registerCard >> {}", result);
        logger.debug("customerUid[{}]", customerUid);

        logger.debug("------2. 즉시결제-----------------------------");
        String merchantUid = "systom-" + System.nanoTime();
        String amount = "100";
        String description = "OO월 멤버십비용";
        result = service.makePayment(customerUid, merchantUid, amount, description);
        logger.debug("makePayment >> {}", result);

        logger.debug("------3. 결제스케줄 및 완료-----------------------------");
        Calendar nextDateTime = Calendar.getInstance();
        //1달후 예약.
        nextDateTime.add(Calendar.MONTH, 1);
        merchantUid = "systom-" + System.nanoTime();
        result = service.schedulePayment(customerUid, merchantUid, amount, description, nextDateTime);
        logger.debug("schedulePayment >> {}", result);
        logger.debug(">> 5초후 취소합니다.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("------4. 스케줄취소-----------------------------");
        result = service.cancelScheduledPayment(customerUid);
        logger.debug("cancelScheduledPayment >> {}", result);
        logger.debug("카드 {}의 모든 스케줄이 취소되었습니다.", customerUid);


        logger.debug("------5. 결제스케쥴-----------------------------");
        nextDateTime = Calendar.getInstance();
        //30초후 예약.
        nextDateTime.add(Calendar.SECOND, 30);
        merchantUid = "systom-" + System.nanoTime();
        result = service.schedulePayment(customerUid, merchantUid, amount, description, nextDateTime);
        logger.debug("schedulePayment >> {}", result);
        logger.debug(">> 결제될때까지 40초간 대기합니다.");
        try {
            Thread.sleep(40* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.debug("------6. 카드삭제-----------------------------");
        result = service.unregisterCard(customerUid);
        logger.debug("unregistercard >> {}", result);

        logger.debug("------끝-----------------------------");
    }
}
