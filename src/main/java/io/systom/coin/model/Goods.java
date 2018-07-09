package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 21.
 * 
 */
public class Goods {

    private Integer id;
    private Integer strategyId;
    private Integer version;
    private String exchange;
    private String coinUnit;
    private String baseUnit;
    private String name;
    private String description;
    private Float cash;
    private String cashUnit;
    private Boolean isDisplay;
    private Integer recruitStart;
    private Integer recruitEnd;
    private Integer investStart;
    private Integer investEnd;
    private Integer testStart;
    private Integer testEnd;
    private String userId;
    private Date createTime;
    private Float testReturnPct;
    private String testMonthlyReturn;
    private Float investGoods;
    private Integer investId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCoinUnit() {
        return coinUnit;
    }

    public void setCoinUnit(String coinUnit) {
        this.coinUnit = coinUnit;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public Boolean getDisplay() {
        return isDisplay;
    }

    public void setDisplay(Boolean display) {
        isDisplay = display;
    }

    public Integer getRecruitStart() {
        return recruitStart;
    }

    public void setRecruitStart(Integer recruitStart) {
        this.recruitStart = recruitStart;
    }

    public Integer getRecruitEnd() {
        return recruitEnd;
    }

    public void setRecruitEnd(Integer recruitEnd) {
        this.recruitEnd = recruitEnd;
    }

    public Integer getInvestStart() {
        return investStart;
    }

    public void setInvestStart(Integer investStart) {
        this.investStart = investStart;
    }

    public Integer getInvestEnd() {
        return investEnd;
    }

    public void setInvestEnd(Integer investEnd) {
        this.investEnd = investEnd;
    }

    public Integer getTestStart() {
        return testStart;
    }

    public void setTestStart(Integer testStart) {
        this.testStart = testStart;
    }

    public Integer getTestEnd() {
        return testEnd;
    }

    public void setTestEnd(Integer testEnd) {
        this.testEnd = testEnd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Float getInvestGoods() {
        return investGoods;
    }

    public void setInvestGoods(Float investGoods) {
        this.investGoods = investGoods;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Float getTestReturnPct() {
        return testReturnPct;
    }

    public void setTestReturnPct(Float testReturnPct) {
        this.testReturnPct = testReturnPct;
    }

    public String getTestMonthlyReturn() {
        return testMonthlyReturn;
    }

    public void setTestMonthlyReturn(String testMonthlyReturn) {
        this.testMonthlyReturn = testMonthlyReturn;
    }
}