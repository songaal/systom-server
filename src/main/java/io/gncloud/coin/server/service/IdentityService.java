package io.gncloud.coin.server.service;

import io.gncloud.coin.server.ws.WebSocketSessionInfo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 권한, 인증 및 세션 관리 서비스
 *
 * */
@Service
public class IdentityService {

    private Map<String, ConcurrentSkipListSet<WebSocketSessionInfo>> subscriberMap;

    public Map<String, ConcurrentSkipListSet<WebSocketSessionInfo>> getSubscriberMap() {
        return subscriberMap;
    }
}
