package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceDaily {
    private Integer id;
    private String date;
    private float marketPrice;
    private float cash;
    private float coin;
    private float base;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(float marketPrice) {
        this.marketPrice = marketPrice;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
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

    @Override
    public String toString() {
        return "PerformanceDaily{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", marketPrice=" + marketPrice +
                ", cash=" + cash +
                ", coin=" + coin +
                ", base=" + base +
                ", equity=" + equity +
                ", dailyReturn=" + dailyReturn +
                ", dailyReturnPct=" + dailyReturnPct +
                ", cumReturn=" + cumReturn +
                ", cumReturnPct=" + cumReturnPct +
                ", updated=" + updated +
                '}';
    }
}