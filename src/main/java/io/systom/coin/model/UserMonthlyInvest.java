package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class UserMonthlyInvest {

    private String userId;
    private String date;
    private String cashUnit;
    private String initCash;
    private String monthlyReturn;
    private float monthlyReturnPct;
    private Date updated;
    private String equity;

    private float sumUsdtInitCash;
    private float sumKrwInitCash;

    private float sumUsdtReturn;
    private float sumKrwReturn;

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

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public String getInitCash() {
        return initCash;
    }

    public void setInitCash(String initCash) {
        this.initCash = initCash;
    }

    public String getMonthlyReturn() {
        return monthlyReturn;
    }

    public void setMonthlyReturn(String monthlyReturn) {
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

    public String getEquity() {
        return equity;
    }

    public void setEquity(String equity) {
        this.equity = equity;
    }

    public float getSumUsdtInitCash() {
        return sumUsdtInitCash;
    }

    public void setSumUsdtInitCash(float sumUsdtInitCash) {
        this.sumUsdtInitCash = sumUsdtInitCash;
    }

    public float getSumKrwInitCash() {
        return sumKrwInitCash;
    }

    public void setSumKrwInitCash(float sumKrwInitCash) {
        this.sumKrwInitCash = sumKrwInitCash;
    }

    public float getSumUsdtReturn() {
        return sumUsdtReturn;
    }

    public void setSumUsdtReturn(float sumUsdtReturn) {
        this.sumUsdtReturn = sumUsdtReturn;
    }

    public float getSumKrwReturn() {
        return sumKrwReturn;
    }

    public void setSumKrwReturn(float sumKrwReturn) {
        this.sumKrwReturn = sumKrwReturn;
    }

    @Override
    public String toString() {
        return "UserMonthlyInvest{" +
                "userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", cashUnit='" + cashUnit + '\'' +
                ", initCash='" + initCash + '\'' +
                ", monthlyReturn='" + monthlyReturn + '\'' +
                ", monthlyReturnPct=" + monthlyReturnPct +
                ", updated=" + updated +
                ", equity='" + equity + '\'' +
                '}';
    }
}