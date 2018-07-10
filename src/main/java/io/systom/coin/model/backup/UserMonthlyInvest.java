package io.systom.coin.model.backup;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
public class UserMonthlyInvest {

    private Integer userId;
    private String date;
    private float cash;
    private float profit;
    private float profitPct;
    private Date updated;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
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