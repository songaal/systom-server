package io.gncloud.coin.server.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 래핑클래스
 * */
public class WebSocketSessionInfoSet extends ConcurrentSkipListSet<WebSocketSessionInfo> {

    private static Logger logger = LoggerFactory.getLogger(WebSocketSessionInfoSet.class);

    public void sendTextMessage(TextMessage message) {
        for (WebSocketSessionInfo session : this) {
            try {
                session.getSession().sendMessage(message);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }
}
