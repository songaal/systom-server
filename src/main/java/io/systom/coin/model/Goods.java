package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 21.
 * 
 */
public class Goods {

    private Integer id;
    private Integer strategyId;
    private Integer version;
    private String exchange;
    private String symbol;
    private String timeInterval;
    private String description;
    private float maxLimit;
    private boolean isDisplay;
    private String exchangeKey;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(float maxLimit) {
        this.maxLimit = maxLimit;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }

    public String getExchangeKey() {
        return exchangeKey;
    }

    public void setExchangeKey(String exchangeKey) {
        this.exchangeKey = exchangeKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}