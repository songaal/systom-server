package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceDaily {
    private Integer investId;
    private String date;
    private float marketPrice;
    private float cash;
    private float coin;
    private float base;
    private float equity;
    private float returns;
    private float returnsPct;
    private float cumReturn;
    private float cumReturnPct;
    private Date updateTime;

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
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

    public float getReturns() {
        return returns;
    }

    public void setReturns(float returns) {
        this.returns = returns;
    }

    public float getReturnsPct() {
        return returnsPct;
    }

    public void setReturnsPct(float returnsPct) {
        this.returnsPct = returnsPct;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "PerformanceDaily{" +
                "investId=" + investId +
                ", date='" + date + '\'' +
                ", marketPrice=" + marketPrice +
                ", cash=" + cash +
                ", coin=" + coin +
                ", base=" + base +
                ", equity=" + equity +
                ", returns=" + returns +
                ", returnsPct=" + returnsPct +
                ", cumReturn=" + cumReturn +
                ", cumReturnPct=" + cumReturnPct +
                ", updateTime=" + updateTime +
                '}';
    }
}