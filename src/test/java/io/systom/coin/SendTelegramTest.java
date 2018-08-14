package io.systom.coin;

import io.systom.coin.model.Telegram;
import io.systom.coin.scheduler.NotificationScheduler;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.TelegramHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SendTelegramTest {

    @Autowired
    private TelegramHandler telegramHandler;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private NotificationScheduler notificationScheduler;

    @Test
    public void telegramTest () {
        telegramHandler.goodsNotification(3, "", "[테스트]상품 투자시작되었습니다.");

    }

    @Test
    public void telegramSendTest () {
        Telegram telegram = new Telegram();
        telegram.setOrigin("SYSTOM");
        telegram.setMessage("변동성 돌파 상품 투자 시작되었습니다.");
        telegram.setTimestamp(new Date().getTime());
        telegram.setUser("455272535");
        telegramHandler.send(telegram);

    }

    @Test
    public void getTest() {
//        List<Integer> idList = goodsService.selectGoodsIdList("collect_start", "20180813");
//        System.out.println("idList" + idList);

        notificationScheduler.task();
    }

}