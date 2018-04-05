package io.gncloud.coin.server.config;

import io.gncloud.coin.server.service.IdentityService;
import io.gncloud.coin.server.ws.EventWebSocketHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private IdentityService identityService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler( new EventWebSocketHandler(identityService)
                , "/agent/*" /* 라이브 트레이딩 에이전트용도 */
                , "/test/*") /* 백테스트용도 */
                .setAllowedOrigins("*");
    }

}
