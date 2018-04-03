package io.gncloud.coin.server.ws;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketSessionInfo implements Comparable<WebSocketSessionInfo> {

    private String sessionId;

    private WebSocketSession session;

    public WebSocketSessionInfo(WebSocketSession session) {
        this.session = session;
        this.sessionId = session.getId();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public int compareTo(WebSocketSessionInfo o) {
        return sessionId.compareTo(o.sessionId);
    }

    @Override
    public String toString() {
        return "WebSocketSessionInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", session=" + session +
                '}';
    }
}
