package io.gncloud.coin.server.service.telegram;

import io.gncloud.coin.server.service.IdentityService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
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
@Profile("telegram")
@Service
public class EventWatchTelegramService {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventWatchTelegramService.class);

    private EventNotifyBot bot;
    private IdentityService identityService;

    @Value("${notify.telegram.botName}")
    private String botName;

    @Value("${notify.telegram.botToken}")
    private String botToken;

    private Map<String, Long> userChatIdMap;
    private Map<Long, String> chatIdUserMap;

    @PostConstruct
    public void init(){
        logger.info("Initialize TelegramBot.. {}, {}", botName, botToken);
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            bot = new EventNotifyBot(this, identityService, botName, botToken);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("", e);
        }

        userChatIdMap = new HashMap<>();
        chatIdUserMap = new HashMap<>();
        //tODO 초기에 어디선가 로딩한다..  userId, chatId
    }

    public void setUserChatId(String userId, Long chatId) {
        // 메모리 저장.
        userChatIdMap.put(userId, chatId);
        chatIdUserMap.put(chatId, userId);
        // 영구저장소 저장.
        //TODO 어딘가에 저장한다..  userId, chatId
    }

    public void sendMessage(String userId, String text){
        Long chatId = userChatIdMap.get(userId);
        if(chatId != null) {
            bot.sendMessage(chatId, text);
        }
    }

    public String getUserByTelegram(long chatId) {
        return chatIdUserMap.get(chatId);
    }

    public void unsetUserChatId(long chatId) {
        String userId = chatIdUserMap.get(chatId);


        //TODO unset userId, chatId
    }
}