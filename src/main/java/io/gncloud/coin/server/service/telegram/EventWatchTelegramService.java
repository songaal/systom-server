package io.gncloud.coin.server.service.telegram;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */

@Service
public class EventWatchTelegramService {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventWatchTelegramService.class);

    private EventNotifyBot bot;

    @Value("${notify.telegram.botName}")
    private String botName;

    @Value("${notify.telegram.botToken}")
    private String botToken;

    private Map<String, String> userChatIdMap;

    @PostConstruct
    public void init(){
        logger.info("Construct TelegramBot. {}, {}", botName, botToken);
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            bot = new EventNotifyBot(this, botName, botToken);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("", e);
        }

        userChatIdMap = new HashMap<>();
        //tODO 초기에 어디선가 로딩한다..
    }

    public void setUserChatId(String userId, String chatId) {
        userChatIdMap.put(userId, chatId);
        //TODO 어딘가에 저장한다..
    }
    public String getChatIdByUserId(String userId) {
        return userChatIdMap.get(userId);
    }

    public void sendMessage(String chatId, String text){
        bot.sendMessage(chatId, text);
    }

}