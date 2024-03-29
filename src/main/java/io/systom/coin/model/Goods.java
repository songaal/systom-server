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
    private String testStart;
    private String testEnd;
    private String authorId;
    private Date createTime;
    private String testResult;
    /* 가상투자자. 상품생성시 자동으로 참가하는 invest_id */
    private Integer publicInvestId;
    private String userId;
    private float investCash;
    private Integer investId;
    private boolean isTaskRunning;
    private String taskEcsId;
    private TraderTask waitTask;
    private String status;
    private String timezone;

    private List<TraderTaskResult.Result.Trade> tradeHistory;
    private List<TraderTaskResult.Result.Trade> publicTradeHistory;
    private List<MonthlyReturn> publicMonthlyReturnsPct;
    private float publicReturnsPct;
    private float publicMdd;

    private PerformanceSummary performanceSummary;
    private List<PerformanceDaily> performanceDaily;
    private TradeStat tradeStat;
    private Date investTime;

    private boolean isPaper;
    private boolean isFinished;
    private Date endTime;
    private boolean isExecuting;

    private Integer paperCount;
    private Integer investCount;
    private Integer totalCount;
    private List<InvestGoods> investors;

    public List<InvestGoods> getInvestors() {
        return investors;
    }

    public void setInvestors(List<InvestGoods> investors) {
        this.investors = investors;
    }

    public Integer getPaperCount() {
        return paperCount;
    }

    public void setPaperCount(Integer paperCount) {
        this.paperCount = paperCount;
    }

    public Integer getInvestCount() {
        return investCount;
    }

    public void setInvestCount(Integer investCount) {
        this.investCount = investCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public float getPublicReturnsPct() {
        return publicReturnsPct;
    }

    public void setPublicReturnsPct(float publicReturnsPct) {
        this.publicReturnsPct = publicReturnsPct;
    }

    public float getPublicMdd() {
        return publicMdd;
    }

    public void setPublicMdd(float publicMdd) {
        this.publicMdd = publicMdd;
    }

    public List<MonthlyReturn> getPublicMonthlyReturnsPct() {
        return publicMonthlyReturnsPct;
    }

    public void setPublicMonthlyReturnsPct(List<MonthlyReturn> publicMonthlyReturnsPct) {
        this.publicMonthlyReturnsPct = publicMonthlyReturnsPct;
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    public void setExecuting(boolean executing) {
        isExecuting = executing;
    }

    public List<TraderTaskResult.Result.Trade> getPublicTradeHistory() {
        return publicTradeHistory;
    }

    public void setPublicTradeHistory(List<TraderTaskResult.Result.Trade> publicTradeHistory) {
        this.publicTradeHistory = publicTradeHistory;
    }

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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public List<TraderTaskResult.Result.Trade> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(List<TraderTaskResult.Result.Trade> tradeHistory) {
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

    public float getInvestCash() {
        return investCash;
    }

    public void setInvestCash(float investCash) {
        this.investCash = investCash;
    }

    public String getTaskEcsId() {
        return taskEcsId;
    }

    public void setTaskEcsId(String taskEcsId) {
        this.taskEcsId = taskEcsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TraderTask getWaitTask() {
        return waitTask;
    }

    public void setWaitTask(TraderTask waitTask) {
        this.waitTask = waitTask;
    }

    public boolean isTaskRunning() {
        return isTaskRunning;
    }

    public void setTaskRunning(boolean taskRunning) {
        isTaskRunning = taskRunning;
    }

    public TradeStat getTradeStat() {
        return tradeStat;
    }

    public void setTradeStat(TradeStat tradeStat) {
        this.tradeStat = tradeStat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getInvestTime() {
        return investTime;
    }

    public void setInvestTime(Date investTime) {
        this.investTime = investTime;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public void setPaper(boolean paper) {
        isPaper = paper;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPublicInvestId() {
        return publicInvestId;
    }

    public void setPublicInvestId(Integer publicInvestId) {
        this.publicInvestId = publicInvestId;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", strategyId=" + strategyId +
                ", version=" + version +
                ", exchange='" + exchange + '\'' +
                ", coinUnit='" + coinUnit + '\'' +
                ", baseUnit='" + baseUnit + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cash=" + cash +
                ", cashUnit='" + cashUnit + '\'' +
                ", isDisplay=" + isDisplay +
                ", testStart='" + testStart + '\'' +
                ", testEnd='" + testEnd + '\'' +
                ", authorId='" + authorId + '\'' +
                ", createTime=" + createTime +
                ", testResult='" + testResult + '\'' +
                ", userId='" + userId + '\'' +
                ", investCash=" + investCash +
                ", investId=" + investId +
                ", isTaskRunning=" + isTaskRunning +
                ", taskEcsId='" + taskEcsId + '\'' +
                ", waitTask=" + waitTask +
                ", status='" + status + '\'' +
                ", timezone='" + timezone + '\'' +
                ", tradeHistory=" + tradeHistory +
                ", performanceSummary=" + performanceSummary +
                ", performanceDaily=" + performanceDaily +
                ", tradeStat=" + tradeStat +
                ", investTime=" + investTime +
                ", isPaper=" + isPaper +
                ", isFinished=" + isFinished +
                ", endTime=" + endTime +
                '}';
    }
}