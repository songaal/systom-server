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
    private float monthlyReturn;
    private float monthlyReturnPct;
    private Date updated;

    private float sumCash;

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