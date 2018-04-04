package io.gncloud.coin.server.ws;

import com.google.gson.Gson;
import io.gncloud.coin.server.service.IdentityService;
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
import java.util.concurrent.ConcurrentSkipListSet;

public class EventWebSocketHandler extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(EventWebSocketHandler.class);

    private Gson gson;
    private IdentityService identityService;
    private Map<String, ConcurrentSkipListSet<io.gncloud.coin.server.ws.WebSocketSessionInfo>> subscriberMap;

    private EventWebSocketHandler(){}

    public EventWebSocketHandler(IdentityService identityService) {
        gson = new Gson();
        this.identityService = identityService;
        this.subscriberMap  = identityService.getSubscriberMap();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //TODO subscribe 연결 및 라스트 데이터 전송
        //TODO unsubscribe 연결 종료 처리
        logger.debug("메시지 수신 : {}", message.getPayload());

        CandleMessage candleMessage = gson.fromJson(message.getPayload(), CandleMessage.class);
        if(candleMessage == null || candleMessage.getType() == null){
            failMessage(session, "invalid Request");
            return;
        }

        switch (candleMessage.getType()){
            case fetch:
                //TODO 코인,인터벌의 시작시간, 종료시간 또는 시작시간 으로 초기 데이터용
                session.sendMessage(new TextMessage("fetch data!!"));
                break;
            case subscribe:
                addedSubscriberMap(session, candleMessage);
                break;
            case unSubscribe:
                removeSubscribe(session, candleMessage);
                break;
        }

//        참고용
//        session.getAttributes();
//        subscriberMap.put("channel", set);
//        set.add(pathKey);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO [사용안함] 메시지를 통해 세션 저장 할것임.
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Iterator<Map.Entry<String, ConcurrentSkipListSet<WebSocketSessionInfo>>> entryIterator =  subscriberMap.entrySet().iterator();
        while(entryIterator.hasNext()){
            Map.Entry<String, ConcurrentSkipListSet<WebSocketSessionInfo>> entry = entryIterator.next();
            Iterator<WebSocketSessionInfo> iterator = entry.getValue().iterator();
            while(iterator.hasNext()){
                WebSocketSessionInfo sessionInfo = iterator.next();
                if(sessionInfo.getSessionId().equals(session.getId())){
                    iterator.remove();
                    logger.debug("세션 객체 삭제. 세션 아이디 : {}, ", session.getId());
                }
            }
        }
    }

    // 연결 추가
    private void addedSubscriberMap(WebSocketSession session, CandleMessage candleMessage){
        String key = PathKey.getKey(candleMessage);
        ConcurrentSkipListSet<WebSocketSessionInfo> sessionInfoConcurrentSkipListSet = subscriberMap.get(key);
        if(sessionInfoConcurrentSkipListSet == null){
            sessionInfoConcurrentSkipListSet = new ConcurrentSkipListSet<>();
        }
        sessionInfoConcurrentSkipListSet.add(new WebSocketSessionInfo(session));
        logger.info("New Session added {}. Path: {}", session.getId(), key);
    }

    // 삭제 대기 추가.
    private void removeSubscribe(WebSocketSession session, CandleMessage wsMessage){
        String key = PathKey.getKey(wsMessage);
        ConcurrentSkipListSet<WebSocketSessionInfo> subScribeList = getSubscribeList(key);
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
    public ConcurrentSkipListSet<WebSocketSessionInfo> getSubscribeList(String key){
        Set<Map.Entry<String, ConcurrentSkipListSet<WebSocketSessionInfo>>> subscribeSet = subscriberMap.entrySet();
        Iterator<Map.Entry<String, ConcurrentSkipListSet<WebSocketSessionInfo>>> iterator = subscribeSet.iterator();
        ConcurrentSkipListSet<WebSocketSessionInfo> tmpSubscribeSet = null;
        while(iterator.hasNext()){
            Map.Entry<String, ConcurrentSkipListSet<WebSocketSessionInfo>> subscribe = iterator.next();
            if(key.equals(subscribe.getKey())){
                tmpSubscribeSet = subscribe.getValue();
                break;
            }
        }
        return tmpSubscribeSet;
    }

    private WebSocketSessionInfo getWebSocketSessionInfo(ConcurrentSkipListSet<WebSocketSessionInfo> WebSocketSessionInfoSet, WebSocketSession session){
        Iterator<WebSocketSessionInfo> sessionInfoIterator = WebSocketSessionInfoSet.iterator();
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
