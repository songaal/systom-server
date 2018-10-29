package io.systom.coin.model;

public class UserMonthlySum {

    private Float usdtInitCash;
    private Float krwInitCash;
    private Float usdtReturn;
    private Float krwReturn;
    private Float returnPct;
    private String date;
    private String userId;
    private Float currencyRate;

    public Float getUsdtInitCash() {
        return usdtInitCash;
    }

    public void setUsdtInitCash(Float usdtInitCash) {
        this.usdtInitCash = usdtInitCash;
    }

    public Float getKrwInitCash() {
        return krwInitCash;
    }

    public void setKrwInitCash(Float krwInitCash) {
        this.krwInitCash = krwInitCash;
    }

    public Float getUsdtReturn() {
        return usdtReturn;
    }

    public void setUsdtReturn(Float usdtReturn) {
        this.usdtReturn = usdtReturn;
    }

    public Float getKrwReturn() {
        return krwReturn;
    }

    public void setKrwReturn(Float krwReturn) {
        this.krwReturn = krwReturn;
    }

    public Float getReturnPct() {
        return returnPct;
    }

    public void setReturnPct(Float returnPct) {
        this.returnPct = returnPct;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Float getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Float currencyRate) {
        this.currencyRate = currencyRate;
    }
}
