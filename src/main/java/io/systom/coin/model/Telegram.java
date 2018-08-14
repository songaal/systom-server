package io.systom.coin.model;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
public class Telegram {

    private String userId;
    private String user;
    private long timestamp;
    private String origin;
    private String message;
    private boolean notify;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public String toString() {
        return "Telegram{" +
                "userId='" + userId + '\'' +
                ", user='" + user + '\'' +
                ", timestamp=" + timestamp +
                ", origin='" + origin + '\'' +
                ", message='" + message + '\'' +
                ", notify=" + notify +
                '}';
    }
}