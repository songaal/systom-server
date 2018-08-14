package io.systom.coin.scheduler;

import io.systom.coin.model.Goods;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.TelegramHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@Component
public class NotificationScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private TelegramHandler telegramHandler;
    @Autowired
    private GoodsService goodsService;

    @Value("${scheduler.isNotification}")
    private String isNotification;

    private String origin = "SYSTOM";
    private String startCollectMessage = "%s 상품 모집이 시작되었습니다.";
    private String endCollectMessage = "%s 상품 모집이 종료되었습니다.";
    private String startInvestMessage = "%s 상품 투자가 시작되었습니다.";
    private String endInvestMessage = "%s 상품 투자가 종료되었습니다.";

    private Map<String, Object> taskStatus = new HashMap<>();

    @Scheduled(cron = "* 14 * * * *")
    public void task() {
        if(!"true".equalsIgnoreCase(isNotification)) {
            logger.debug("Notification Disabled");
            return;
        }

        logger.debug("============= 텔레그램 메시지 전송을 시작합니다. =============");

        String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        List<Goods> goodsList = null;
        logger.debug("텔레그램 전송 시간: {}", nowDate);

        goodsList = goodsService.selectGoodsIdList("collect_start", nowDate);
        if (goodsList != null) {
            int goodsSize = goodsList.size();
            for (int i=0; i < goodsSize; i++) {
                Goods goods = goodsList.get(i);
                String message = String.format(startCollectMessage, goods.getName());
                telegramHandler.goodsNotification(goods.getId(), origin, message);
            }
            logger.debug("모집 시작 상품 수: {}", goodsList.size());
        }


        goodsList = goodsService.selectGoodsIdList("collect_end", nowDate);
        if (goodsList != null) {
            int goodsSize = goodsList.size();
            for (int i=0; i < goodsSize; i++) {
                Goods goods = goodsList.get(i);
                String message = String.format(endCollectMessage, goods.getName());
                telegramHandler.goodsNotification(goods.getId(), origin, message);
            }
            logger.debug("모집 종료 상품 수: {}", goodsList.size());
        }

        goodsList = goodsService.selectGoodsIdList("invest_start", nowDate);
        if (goodsList != null) {
            int goodsSize = goodsList.size();
            for (int i=0; i < goodsSize; i++) {
                Goods goods = goodsList.get(i);
                String message = String.format(startInvestMessage, goods.getName());
                telegramHandler.goodsNotification(goods.getId(), origin, message);
            }
            logger.debug("투자 시작 상품 수: {}", goodsList.size());
        }

        goodsList = goodsService.selectGoodsIdList("invest_end", nowDate);
        if (goodsList != null) {
            int goodsSize = goodsList.size();
            for (int i=0; i < goodsSize; i++) {
                Goods goods = goodsList.get(i);
                String message = String.format(endInvestMessage, goods.getName());
                telegramHandler.goodsNotification(goods.getId(), origin, message);
            }
            logger.debug("투자 종료 상품 수: {}", goodsList.size());
        }

        logger.debug("============= 텔레그램 메시지 전송을 종료합니다. =============");
    }



}