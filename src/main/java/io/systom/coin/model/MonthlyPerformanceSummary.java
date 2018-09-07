package io.systom.coin.model;

/*
 * create joonwoo 2018. 9. 6.
 * 
 */
public class MonthlyPerformanceSummary {
    private float initCash;
    private float equity;
    private String cashUnit;
    private String date;

    public float getInitCash() {
        return initCash;
    }

    public void setInitCash(float initCash) {
        this.initCash = initCash;
    }

    public float getEquity() {
        return equity;
    }

    public void setEquity(float equity) {
        this.equity = equity;
    }

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}