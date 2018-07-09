package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class UserMonthlyInvest {

    private String userId;
    private String date;
    private Float cash;
    private float profit;
    private float profitPct;
    private Date updated;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public float getProfitPct() {
        return profitPct;
    }

    public void setProfitPct(float profitPct) {
        this.profitPct = profitPct;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}