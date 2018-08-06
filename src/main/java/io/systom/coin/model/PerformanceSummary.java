package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 7. 9.
 * 
 */
public class PerformanceSummary {
    private Integer investId;
    private float cash;
    private float initCash;
    private float maxReturnsPct;
    private float equity;
    private float returns;
    private float returnsPct;
    private float mdd;
    private Date createTime;
    private String commission;
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

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public float getInitCash() {
        return initCash;
    }

    public void setInitCash(float initCash) {
        this.initCash = initCash;
    }

    public float getMaxReturnsPct() {
        return maxReturnsPct;
    }

    public void setMaxReturnsPct(float maxReturnsPct) {
        this.maxReturnsPct = maxReturnsPct;
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

    public float getReturnsPct() {
        return returnsPct;
    }

    public void setReturnsPct(float returnsPct) {
        this.returnsPct = returnsPct;
    }

    public float getMdd() {
        return mdd;
    }

    public void setMdd(float mdd) {
        this.mdd = mdd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
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

    @Override
    public String toString() {
        return "PerformanceSummary{" +
                "investId=" + investId +
                ", cash=" + cash +
                ", initCash=" + initCash +
                ", maxReturnsPct=" + maxReturnsPct +
                ", equity=" + equity +
                ", returns=" + returns +
                ", returnsPct=" + returnsPct +
                ", mdd=" + mdd +
                ", createTime=" + createTime +
                ", commission='" + commission + '\'' +
                ", coinInvested=" + coinInvested +
                ", baseInvested=" + baseInvested +
                ", positions='" + positions + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}