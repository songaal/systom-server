package io.systom.coin.service;

import io.systom.coin.model.Telegram;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@Component
public class TelegramHandler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TelegramHandler.class);
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private GoodsService goodsService;

    @Value("${notifications.telegramUrl}")
    private String telegramUrl;

    public void goodsNotification (Integer goodsId, String origin, String message) {
        List<String> managers = identityService.getManager();
        List<String> investors = goodsService.selectInvestUserId(goodsId);
        List<String> targetList = new ArrayList<>();
        targetList.addAll(managers);
        if (investors != null) {
            targetList.addAll(investors);
        } else {
            logger.debug("전달 대상이 없습니다.");
        }

        List<Telegram> telegramList = sqlSession.selectList("notification.retrieveTelegram", targetList);
        if (telegramList == null) {
            logger.debug("전달 대상이 없습니다.");
            return;
        }
        Iterator<Telegram> iterator = telegramList.iterator();
        while(iterator.hasNext()) {
            Telegram telegram = iterator.next();
            telegram.setOrigin(origin);
            telegram.setMessage(message);
            send(telegram);
        }
    }

    public void adminSend(String origin, String message) {
        List<Telegram> telegramList = sqlSession.selectList("notification.retrieveTelegram", identityService.getManager());
        if (telegramList == null) {
            logger.debug("전달 대상이 없습니다.");
            return;
        }
        Iterator<Telegram> iterator = telegramList.iterator();
        while(iterator.hasNext()) {
            Telegram telegram = iterator.next();
            telegram.setOrigin(origin);
            telegram.setMessage(message);
            send(telegram);
        }
    }

    public String send(Telegram telegram) {
        if (telegram.getTimestamp() == 0) {
            telegram.setTimestamp(new Date().getTime());
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(telegramUrl, telegram, String.class);
        } catch (Exception e) {
            logger.debug("[FAIL] 텔레그램 전송실패 : {}", telegram);
            return null;
        }
    }

}