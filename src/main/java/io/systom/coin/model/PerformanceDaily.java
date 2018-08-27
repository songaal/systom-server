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
    private float equity;
    private float returns;
    private float returnsPct;
    private float cumReturns;
    private float cumReturnsPct;
    private float hwm;
    private float prevEquity;
    private float prevHwm;
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

    public float getCumReturns() {
        return cumReturns;
    }

    public void setCumReturns(float cumReturns) {
        this.cumReturns = cumReturns;
    }

    public float getCumReturnsPct() {
        return cumReturnsPct;
    }

    public void setCumReturnsPct(float cumReturnsPct) {
        this.cumReturnsPct = cumReturnsPct;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public float getHwm() {
        return hwm;
    }

    public void setHwm(float hwm) {
        this.hwm = hwm;
    }

    public float getPrevEquity() {
        return prevEquity;
    }

    public void setPrevEquity(float prevEquity) {
        this.prevEquity = prevEquity;
    }

    public float getPrevHwm() {
        return prevHwm;
    }

    public void setPrevHwm(float prevHwm) {
        this.prevHwm = prevHwm;
    }

    @Override
    public String toString() {
        return "PerformanceDaily{" +
                "investId=" + investId +
                ", date='" + date + '\'' +
                ", marketPrice=" + marketPrice +
                ", equity=" + equity +
                ", returns=" + returns +
                ", returnsPct=" + returnsPct +
                ", cumReturns=" + cumReturns +
                ", cumReturnsPct=" + cumReturnsPct +
                ", hwm=" + hwm +
                ", prevEquity=" + prevEquity +
                ", prevHwm=" + prevHwm +
                ", updateTime=" + updateTime +
                '}';
    }
}