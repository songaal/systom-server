package io.systom.coin.model;

import java.util.Date;
import java.util.List;

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
    private String recruitStart;
    private String recruitEnd;
    private String investStart;
    private String investEnd;
    private String testStart;
    private String testEnd;
    private String userId;
    private Date createTime;
    private Float testReturnPct;
    private String testMonthlyReturn;

    private Integer investId;
    private List<Trade> tradeHistory;
    private PerformanceSummary performanceSummary;
    private List<PerformanceDaily> performanceDaily;

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

    public String getRecruitStart() {
        return recruitStart;
    }

    public void setRecruitStart(String recruitStart) {
        this.recruitStart = recruitStart;
    }

    public String getRecruitEnd() {
        return recruitEnd;
    }

    public void setRecruitEnd(String recruitEnd) {
        this.recruitEnd = recruitEnd;
    }

    public String getInvestStart() {
        return investStart;
    }

    public void setInvestStart(String investStart) {
        this.investStart = investStart;
    }

    public String getInvestEnd() {
        return investEnd;
    }

    public void setInvestEnd(String investEnd) {
        this.investEnd = investEnd;
    }

    public String getTestStart() {
        return testStart;
    }

    public void setTestStart(String testStart) {
        this.testStart = testStart;
    }

    public String getTestEnd() {
        return testEnd;
    }

    public void setTestEnd(String testEnd) {
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

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public List<Trade> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(List<Trade> tradeHistory) {
        this.tradeHistory = tradeHistory;
    }

    public PerformanceSummary getPerformanceSummary() {
        return performanceSummary;
    }

    public void setPerformanceSummary(PerformanceSummary performanceSummary) {
        this.performanceSummary = performanceSummary;
    }

    public List<PerformanceDaily> getPerformanceDaily() {
        return performanceDaily;
    }

    public void setPerformanceDaily(List<PerformanceDaily> performanceDaily) {
        this.performanceDaily = performanceDaily;
    }
}