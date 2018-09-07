package io.systom.coin.model;

/*
 * create joonwoo 2018. 9. 7.
 * 
 */
public class DailyInvestState {

    private float initCash;
    private float equity;
    private String date;
    private String cashUnit;

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

    @Override
    public String toString() {
        return "DailyInvestState{" +
                "initCash=" + initCash +
                ", equity=" + equity +
                ", date='" + date + '\'' +
                ", cashUnit='" + cashUnit + '\'' +
                '}';
    }
}