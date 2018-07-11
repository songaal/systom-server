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
    private float monthlyReturn;
    private float monthlyReturnPct;
    private Date updated;

    private float sumCash;

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

    public float getMonthlyReturn() {
        return monthlyReturn;
    }

    public void setMonthlyReturn(float monthlyReturn) {
        this.monthlyReturn = monthlyReturn;
    }

    public float getMonthlyReturnPct() {
        return monthlyReturnPct;
    }

    public void setMonthlyReturnPct(float monthlyReturnPct) {
        this.monthlyReturnPct = monthlyReturnPct;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public float getSumCash() {
        return sumCash;
    }

    public void setSumCash(float sumCash) {
        this.sumCash = sumCash;
    }
}