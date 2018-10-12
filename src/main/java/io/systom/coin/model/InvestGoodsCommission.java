package io.systom.coin.model;

public class InvestGoodsCommission extends InvestGoods{

    private int investDays;
    private float initCash;
    private float entity;
    private float returns;
    private float returnPct;
    private float commission;
    private float totalReturns;
    private String cashUnit;
    private String commUnit;

    public int getInvestDays() {
        return investDays;
    }

    public void setInvestDays(int investDays) {
        this.investDays = investDays;
    }

    public float getInitCash() {
        return initCash;
    }

    public void setInitCash(float initCash) {
        this.initCash = initCash;
    }

    public float getEntity() {
        return entity;
    }

    public void setEntity(float entity) {
        this.entity = entity;
    }

    public float getReturns() {
        return returns;
    }

    public void setReturns(float returns) {
        this.returns = returns;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }

    public float getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(float totalReturns) {
        this.totalReturns = totalReturns;
    }

    public float getReturnPct() {
        return returnPct;
    }

    public void setReturnPct(float returnPct) {
        this.returnPct = returnPct;
    }

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public String getCommUnit() {
        return commUnit;
    }

    public void setCommUnit(String commUnit) {
        this.commUnit = commUnit;
    }

    @Override
    public String toString() {
        return "InvestGoodsCommission{" +
                "investDays=" + investDays +
                ", initCash=" + initCash +
                ", entity=" + entity +
                ", returns=" + returns +
                ", returnPct=" + returnPct +
                ", commission=" + commission +
                ", totalReturns=" + totalReturns +
                ", cashUnit=" + cashUnit +
                ", commUnit=" + commUnit +
                '}';
    }
}
