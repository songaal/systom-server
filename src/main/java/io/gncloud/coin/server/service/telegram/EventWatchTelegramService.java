package io.gncloud.coin.server.service.telegram;

import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.model.Order;
import io.gncloud.coin.server.model.UserNotification;
import io.gncloud.coin.server.service.IdentityService;
import org.apache.ibatis.session.SqlSession;
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
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */
@Profile("telegram")
@Service
public class EventWatchTelegramService {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventWatchTelegramService.class);

    public static final String TELEGRAM_SERVICE = "telegram";

    private EventNotifyBot bot;
    private IdentityService identityService;

    @Value("${notify.telegram.botName}")
    private String botName;

    @Value("${notify.telegram.botToken}")
    private String botToken;

    @Autowired
    private SqlSession sqlSession;

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

    public void setUserChatId(String userId, Long chatId) throws OperationException {
        // 메모리 저장.
        userChatIdMap.put(userId, chatId);
        chatIdUserMap.put(chatId, userId);
        // 영구저장소 저장.
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setServiceName(TELEGRAM_SERVICE);
        notification.setServiceUser(Long.toString(chatId));
        insertNotification(notification);
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

    public void unsetByChatId(long chatId) throws OperationException {
        String userId = chatIdUserMap.remove(chatId);
        if(userId != null) {
            if (userId != null) {
                unsetByUserId(userId);
            }
        } else {
            logger.warn("No such user's chatId[{}]", chatId);
        }
    }

    public void unsetByUserId(String userId) throws OperationException {
        Long chatId = userChatIdMap.remove(userId);
        if(chatId != null) {
            chatIdUserMap.remove(chatId);
            deleteNotification(new UserNotification().withUserId(userId));
        } else {
            logger.warn("No user's telegram. userId[{}]", userId);
        }
    }

    public List<UserNotification> selectList(UserNotification notification) throws OperationException {
        logger.debug("Select UserNotification");
        List<UserNotification> list = null;
        try {
            list = sqlSession.selectList("notification.select", notification);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Select UserNotification");
        }
        return list;
    }

    public UserNotification selectNotification(UserNotification notification) throws OperationException {
        logger.debug("Select 1 UserNotification");
        UserNotification userNotification;
        try {
            userNotification = sqlSession.selectOne("notification.select", notification);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Select 1 UserNotification");
        }
        return userNotification;
    }

    public void insertNotification(UserNotification notification) throws OperationException {
        logger.debug("Insert UserNotification");
        try {
            sqlSession.insert("notification.insert", notification);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Insert UserNotification");
        }
    }


    public void deleteNotification(UserNotification notification) throws OperationException {
        logger.debug("Delete UserNotification");
        try {
            sqlSession.delete("notification.delete", notification);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Delete UserNotification");
        }
    }
}