package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceSummary {
    private Integer id;
    private float coin;
    private float base;
    private float cash;
    private float equity;
    private float dailyReturn;
    private float dailyReturnPct;
    private float cumReturn;
    private float cumReturnPct;
    private Date updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getCoin() {
        return coin;
    }

    public void setCoin(float coin) {
        this.coin = coin;
    }

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public float getEquity() {
        return equity;
    }

    public void setEquity(float equity) {
        this.equity = equity;
    }

    public float getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(float dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public float getDailyReturnPct() {
        return dailyReturnPct;
    }

    public void setDailyReturnPct(float dailyReturnPct) {
        this.dailyReturnPct = dailyReturnPct;
    }

    public float getCumReturn() {
        return cumReturn;
    }

    public void setCumReturn(float cumReturn) {
        this.cumReturn = cumReturn;
    }

    public float getCumReturnPct() {
        return cumReturnPct;
    }

    public void setCumReturnPct(float cumReturnPct) {
        this.cumReturnPct = cumReturnPct;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}