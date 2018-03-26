package io.gncloud.coin.server.model;

/*
 * create joonwoo 2018. 3. 25.
 * 
 */
public class User {

    private String userId;
    private String password;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}