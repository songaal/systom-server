package io.systom.coin.model;

/*
 * create joonwoo 2018. 7. 22.
 * 
 */
public class MonthlyReturn {

    private String date;
    private float returnPct;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getReturnPct() {
        return returnPct;
    }

    public void setReturnPct(float returnPct) {
        this.returnPct = returnPct;
    }
}