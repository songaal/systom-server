//package io.gncloud.coin.server.service;
//
//import OperationException;
//import io.gncloud.coin.server.model.Order;
//import UserNotification;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@Service
//public class EventWatchTelegramService {
//
//    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(EventWatchTelegramService.class);
//
//    public static final String TELEGRAM_SERVICE = "telegram";
//
////    private EventNotifyBot bot;
//    private IdentityService identityService;
//
//    @Value("${notify.telegram.botName}")
//    private String botName;
//
//    @Value("${notify.telegram.botToken}")
//    private String botToken;
//
//    @Value("${notify.telegram.use}")
//    private boolean use;
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    private Map<String, Long> userChatIdMap;
//    private Map<Long, String> chatIdUserMap;
//
//    /**
//     * @throws OperationException
//     */
//    @PostConstruct
//    public void init() throws OperationException {
//        if(!use){
//            logger.info("TelegramBot Diabled.");
//            return;
//        }
//        logger.info("Initialize TelegramBot.. {}, {}", botName, botToken);
////        ApiContextInitializer.init();
////        TelegramBotsApi botsApi = new TelegramBotsApi();
////        try {
////            bot = new EventNotifyBot(this, identityService, botName, botToken);
////            botsApi.registerBot(bot);
////        } catch (TelegramApiException e) {
////            logger.error("", e);
////        }
//
//        //초기로딩.
//        userChatIdMap = new HashMap<>();
//        chatIdUserMap = new HashMap<>();
//
//        List<UserNotification> list = selectAll();
//        if(list != null) {
//            for(UserNotification notification : list) {
//                try {
//                    String userId = notification.getUserId();
//                    Long chatId = Long.parseLong(notification.getServiceUser());
//                    userChatIdMap.put(userId, chatId);
//                    chatIdUserMap.put(chatId, userId);
//                } catch (Exception e) {
//                    logger.error("", e);
//                }
//            }
//        }
//    }
//
//    public void setUserChatId(UserNotification notification) throws OperationException {
//        // 영구저장소 저장.
//        insertNotification(notification);
//        // 메모리 저장.
//        String userId = notification.getUserId();
//        Long chatId = Long.parseLong(notification.getServiceUser());
//        userChatIdMap.put(userId, chatId);
//        chatIdUserMap.put(chatId, userId);
//    }
//
//    public void sendMessage(String userId, String text){
//        Long chatId = userChatIdMap.get(userId);
//        if(chatId != null) {
//            //TODO
//            // 이곳에서는 sqs telegram-bot-coincloud-send 에 메시지를 집어넣는다. 이후 전송은 telegram-server 가 알아서 처리.
//
//
//
//
//        }
//    }
//
//    public void sendMessage(String userId, List<Order> orders){
//
//        orders.forEach(order -> {
//            try {
////                [2018.4.15 17:58:09] poloniex 거래소에서 bts 를  0.00004305 btc 에 1개만큼 구매하였습니다.
//                String msg = "[%s] %s 거래소에서 %s 를  %s %s 에 %s개만큼 %s하였습니다.";
////                long time = order.getTimestamp();
//                Date time = new Date();
//                time.setTime(order.getTimestamp() / 1000000);
//                String formatTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").format(time);
//                msg = String.format( msg
//                                , formatTime
//                                , order.getExchange()
//                                , order.getBase()
//                                , order.getPrice()
//                                , order.getCoin()
//                                , order.getAmount()
//                                , order.getAmount() < 0 ? "판매" : "구매"
//                );
//                sendMessage(userId, msg);
//            } catch (Exception e){
//                logger.error("", e);
//            }
//        });
//    }
//
//    public String getUserByTelegram(long chatId) {
//        return chatIdUserMap.get(chatId);
//    }
//
//    public void unsetByChatId(long chatId) throws OperationException {
//        String userId = chatIdUserMap.remove(chatId);
//        if(userId != null) {
//            if (userId != null) {
//                unsetByUserId(userId);
//            }
//        } else {
//            logger.warn("No such user's chatId[{}]", chatId);
//        }
//    }
//
//    public void unsetByUserId(String userId) throws OperationException {
//        Long chatId = userChatIdMap.remove(userId);
//        if(chatId != null) {
//            chatIdUserMap.remove(chatId);
//            deleteNotification(new UserNotification().withUserId(userId));
//        } else {
//            logger.warn("No user's telegram. userId[{}]", userId);
//        }
//    }
//
//    public List<UserNotification> selectAll() throws OperationException {
//        return selectList(new UserNotification());
//    }
//    public List<UserNotification> selectList(UserNotification notification) throws OperationException {
//        logger.debug("Select UserNotification");
//        List<UserNotification> list = null;
//        try {
//            list = sqlSession.selectList("notification.select", notification);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select UserNotification");
//        }
//        return list;
//    }
//
//    public UserNotification selectNotification(UserNotification notification) throws OperationException {
//        logger.debug("Select 1 UserNotification");
//        UserNotification userNotification;
//        try {
//            userNotification = sqlSession.selectOne("notification.select", notification);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select 1 UserNotification");
//        }
//        return userNotification;
//    }
//
//    public void insertNotification(UserNotification notification) throws OperationException {
//        logger.debug("Insert UserNotification");
//        try {
//            sqlSession.insert("notification.insert", notification);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Insert UserNotification");
//        }
//    }
//
//
//    public void deleteNotification(UserNotification notification) throws OperationException {
//        logger.debug("Delete UserNotification");
//        try {
//            sqlSession.delete("notification.delete", notification);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Delete UserNotification");
//        }
//    }
//}