package io.systom.coin.ws;

import com.google.gson.Gson;
import io.systom.coin.service.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventWebSocketHandler extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(EventWebSocketHandler.class);

    private static final String PATH_BACKTEST = "/backtest";
    private static final String PATH_AGENT = "/agent";

    public static final String KEY_PREFIX_BACKTEST = "t_";
    public static final String KEY_PREFIX_AGENT = "a_";

    private Gson gson;
    private IdentityService identityService;
    private Map<String, WebSocketSessionInfoSet> subscriberMap;

    private EventWebSocketHandler(){}

    public EventWebSocketHandler(IdentityService identityService) {
        gson = new Gson();
        this.identityService = identityService;
        this.subscriberMap  = identityService.getSubscriberMap();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
       // 전송메시지는 무시한다.
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("Websocket connected. Id:{}, Uri:{}, Object:{}", session.getId(), session.getUri(), session);
        String path = session.getUri().getPath();
        String key = null;
        if(path.startsWith(PATH_AGENT)) {
            key = KEY_PREFIX_AGENT + path.substring(PATH_AGENT.length() + 1);
        } else if(path.startsWith(PATH_BACKTEST)) {
            key = KEY_PREFIX_BACKTEST + path.substring(PATH_BACKTEST.length() + 1);
        }

        WebSocketSessionInfoSet value = subscriberMap.get(key);
        if(value == null) {
            value = new WebSocketSessionInfoSet();
            subscriberMap.put(key, value);
        }
        value.add(new WebSocketSessionInfo(session));
    }

    /**
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Iterator<Map.Entry<String, WebSocketSessionInfoSet>> entryIterator =  subscriberMap.entrySet().iterator();
        while(entryIterator.hasNext()){
            Map.Entry<String, WebSocketSessionInfoSet> entry = entryIterator.next();
            Iterator<WebSocketSessionInfo> iterator = entry.getValue().iterator();
            while(iterator.hasNext()){
                WebSocketSessionInfo sessionInfo = iterator.next();
                if(sessionInfo.getSessionId().equals(session.getId())){
                    iterator.remove();
                    logger.debug("세션 객체 삭제. 세션 아이디 : {}, ", session.getId());
                }
            }
            if(entry.getValue().size() == 0) {
                entryIterator.remove();
                logger.debug("세션집합 객체 삭제. 키 : {}, ", entry.getKey());
            }
        }
    }

    // 연결 추가
    private void addedSubscriberMap(WebSocketSession session, CandleMessage candleMessage){
        String key = PathKey.getKey(candleMessage);
        WebSocketSessionInfoSet webSocketSessionInfoSet = subscriberMap.get(key);
        if(webSocketSessionInfoSet == null){
            webSocketSessionInfoSet = new WebSocketSessionInfoSet();
        }
        webSocketSessionInfoSet.add(new WebSocketSessionInfo(session));
        logger.info("New Session added {}. Path: {}", session.getId(), key);
    }

    // 삭제 대기 추가.
    private void removeSubscribe(WebSocketSession session, CandleMessage wsMessage){
        String key = PathKey.getKey(wsMessage);
        WebSocketSessionInfoSet subScribeList = getSubscribeList(key);
        subScribeList.remove(getWebSocketSessionInfo(subScribeList, session));
        logger.info("Remove Wait Session {}. Path: {}", session, PathKey.getKey(wsMessage));
    }

    // 에러 내용 전송용
    protected void failMessage(WebSocketSession session, String error){
        try {
            session.sendMessage(new TextMessage(error));
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    // key에 따른 세션 목록 가져오기
    public WebSocketSessionInfoSet getSubscribeList(String key){
        Set<Map.Entry<String, WebSocketSessionInfoSet>> subscribeSet = subscriberMap.entrySet();
        Iterator<Map.Entry<String, WebSocketSessionInfoSet>> iterator = subscribeSet.iterator();
        WebSocketSessionInfoSet tmpSubscribeSet = null;
        while(iterator.hasNext()){
            Map.Entry<String, WebSocketSessionInfoSet> subscribe = iterator.next();
            if(key.equals(subscribe.getKey())){
                tmpSubscribeSet = subscribe.getValue();
                break;
            }
        }
        return tmpSubscribeSet;
    }

    private WebSocketSessionInfo getWebSocketSessionInfo(WebSocketSessionInfoSet webSocketSessionInfoSet, WebSocketSession session){
        Iterator<WebSocketSessionInfo> sessionInfoIterator = webSocketSessionInfoSet.iterator();
        WebSocketSessionInfo resultSessionInfo = null;
        while(sessionInfoIterator.hasNext()){
            WebSocketSessionInfo tmpWebSocketSessionInfo = sessionInfoIterator.next();
            if(tmpWebSocketSessionInfo.getSessionId().equals(session.getId())){
                resultSessionInfo = tmpWebSocketSessionInfo;
                break;
            }
        }
        return resultSessionInfo;
    }

}
