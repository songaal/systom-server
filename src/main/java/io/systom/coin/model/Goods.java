package io.systom.coin.model;

import java.util.Date;

/*
 * create joonwoo 2018. 6. 21.
 * 
 */
public class Goods {

    private Integer id;
    private String name;
    private Integer strategyId;
    private Integer version;
    private String exchange;
    private String coin;
    private String description;
    private Float amount;
    private Float maxAmount;
    private Float minAmount;
    private String currency;
    private Boolean isDisplay;
    private Long recruitStart;
    private Long recruitEnd;
    private Long investStart;
    private Long investEnd;
    private Long backtestStart;
    private Long backtestEnd;
    private String userId;
    private Date createTime;

    private float recruitAmount;

    private TaskResult.Result performance;

    private boolean isInvest;

    public boolean isInvest() {
        return isInvest;
    }

    public void setInvest(boolean invest) {
        isInvest = invest;
    }

    public TaskResult.Result getPerformance() {
        return performance;
    }

    public void setPerformance(TaskResult.Result performance) {
        this.performance = performance;
    }

    public float getRecruitAmount() {
        return recruitAmount;
    }

    public void setRecruitAmount(float recruitAmount) {
        this.recruitAmount = recruitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }

    public Boolean getDisplay() {
        return isDisplay;
    }

    public void setDisplay(Boolean display) {
        isDisplay = display;
    }

    public Long getRecruitStart() {
        return recruitStart;
    }

    public void setRecruitStart(Long recruitStart) {
        this.recruitStart = recruitStart;
    }

    public Long getRecruitEnd() {
        return recruitEnd;
    }

    public void setRecruitEnd(Long recruitEnd) {
        this.recruitEnd = recruitEnd;
    }

    public Long getInvestStart() {
        return investStart;
    }

    public void setInvestStart(Long investStart) {
        this.investStart = investStart;
    }

    public Long getInvestEnd() {
        return investEnd;
    }

    public void setInvestEnd(Long investEnd) {
        this.investEnd = investEnd;
    }

    public Long getBacktestStart() {
        return backtestStart;
    }

    public void setBacktestStart(Long backtestStart) {
        this.backtestStart = backtestStart;
    }

    public Long getBacktestEnd() {
        return backtestEnd;
    }

    public void setBacktestEnd(Long backtestEnd) {
        this.backtestEnd = backtestEnd;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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
}