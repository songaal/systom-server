package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class UserMonthlyInvest {

    private String userId;
    private String date;
    private float initCash;
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

    public float getInitCash() {
        return initCash;
    }

    public void setInitCash(float initCash) {
        this.initCash = initCash;
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