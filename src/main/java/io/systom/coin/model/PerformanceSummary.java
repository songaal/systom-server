package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceSummary {
    private Integer id;
    private float coin;
    private float base;
    private float cash;
    private float equity;
    private float sumReturn;
    private float sumReturnPct;
    private float mdd;
    private float pnlRate;
    private float avgProfit;
    private float avgLose;
    private int trades;
    private int winCount;
    private int loseCount;
    private Date createTime;
    private float commission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getCoin() {
        return coin;
    }

    public void setCoin(float coin) {
        this.coin = coin;
    }

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public float getEquity() {
        return equity;
    }

    public void setEquity(float equity) {
        this.equity = equity;
    }

    public float getSumReturn() {
        return sumReturn;
    }

    public void setSumReturn(float sumReturn) {
        this.sumReturn = sumReturn;
    }

    public float getSumReturnPct() {
        return sumReturnPct;
    }

    public void setSumReturnPct(float sumReturnPct) {
        this.sumReturnPct = sumReturnPct;
    }

    public float getMdd() {
        return mdd;
    }

    public void setMdd(float mdd) {
        this.mdd = mdd;
    }

    public float getPnlRate() {
        return pnlRate;
    }

    public void setPnlRate(float pnlRate) {
        this.pnlRate = pnlRate;
    }

    public float getAvgProfit() {
        return avgProfit;
    }

    public void setAvgProfit(float avgProfit) {
        this.avgProfit = avgProfit;
    }

    public float getAvgLose() {
        return avgLose;
    }

    public void setAvgLose(float avgLose) {
        this.avgLose = avgLose;
    }

    public int getTrades() {
        return trades;
    }

    public void setTrades(int trades) {
        this.trades = trades;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }
}