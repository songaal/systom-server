package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 8. 6.
 * 
 */
public class TradeStat {
    private Integer investId;
    private int tradeCount;
    private int winCount;
    private int loseCount;
    private float winRate;
    private float profitRateSum;
    private float profitRateAvg;
    private float lossRateSum;
    private float lossRateAvg;
    private float pnlRate;
    private Date updateTime;

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
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

    public float getWinRate() {
        return winRate;
    }

    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }

    public float getProfitRateSum() {
        return profitRateSum;
    }

    public void setProfitRateSum(float profitRateSum) {
        this.profitRateSum = profitRateSum;
    }

    public float getProfitRateAvg() {
        return profitRateAvg;
    }

    public void setProfitRateAvg(float profitRateAvg) {
        this.profitRateAvg = profitRateAvg;
    }

    public float getLossRateSum() {
        return lossRateSum;
    }

    public void setLossRateSum(float lossRateSum) {
        this.lossRateSum = lossRateSum;
    }

    public float getLossRateAvg() {
        return lossRateAvg;
    }

    public void setLossRateAvg(float lossRateAvg) {
        this.lossRateAvg = lossRateAvg;
    }

    public float getPnlRate() {
        return pnlRate;
    }

    public void setPnlRate(float pnlRate) {
        this.pnlRate = pnlRate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TradeStat{" +
                "investId=" + investId +
                ", tradeCount=" + tradeCount +
                ", winCount=" + winCount +
                ", loseCount=" + loseCount +
                ", winRate=" + winRate +
                ", profitRateSum=" + profitRateSum +
                ", profitRateAvg=" + profitRateAvg +
                ", lossRateSum=" + lossRateSum +
                ", lossRateAvg=" + lossRateAvg +
                ", pnlRate=" + pnlRate +
                ", updateTime=" + updateTime +
                '}';
    }
}