package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 4. 12.
 * 
 */
public class ExchangeKey {

    private int id;
    /** userId로 암호화/복호화 하므로 userId 는 필수. */
    private String userId;
    private String exchange;
    private String name;
    private String apiKey;
    private String secretKey;
    private Date createTime;

    public ExchangeKey() {
    }

    public ExchangeKey(int id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}