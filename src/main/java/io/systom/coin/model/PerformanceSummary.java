package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceSummary {
    private Integer investId;
    private float coin;
    private float base;
    private float cash;
    private float initCash;
    private float equity;
    private float returns;
    private float returns_pct;
    private float mdd;
    private float pnlRate;
    private float avgProfit;
    private float avgLose;
    private int trades;
    private int winCount;
    private int loseCount;
    private Date createTime;
    private float commission;
    private boolean coinInvested;
    private boolean baseInvested;
    private String positions;
    private Date updateTime;

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
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

    public float getReturns() {
        return returns;
    }

    public void setReturns(float returns) {
        this.returns = returns;
    }

    public float getReturns_pct() {
        return returns_pct;
    }

    public void setReturns_pct(float returns_pct) {
        this.returns_pct = returns_pct;
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

    public boolean isCoinInvested() {
        return coinInvested;
    }

    public void setCoinInvested(boolean coinInvested) {
        this.coinInvested = coinInvested;
    }

    public boolean isBaseInvested() {
        return baseInvested;
    }

    public void setBaseInvested(boolean baseInvested) {
        this.baseInvested = baseInvested;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public float getInitCash() {
        return initCash;
    }

    public void setInitCash(float initCash) {
        this.initCash = initCash;
    }
}