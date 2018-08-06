package io.systom.coin.model;

import java.util.List;

/*
 * create joonwoo 2018. 7. 23.
 * 
 */
public class GoodsTestResult {
    private Integer id;
    private float testMaxReturnsPct;
    private float testMaxDrawDownPct;
    private List<MonthlyReturn> testMonthlyReturnList;
    private List<TraderTaskResult.Result.Trade> tradeHistory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getTestMaxReturnsPct() {
        return testMaxReturnsPct;
    }

    public void setTestMaxReturnsPct(float testMaxReturnsPct) {
        this.testMaxReturnsPct = testMaxReturnsPct;
    }

    public float getTestMaxDrawDownPct() {
        return testMaxDrawDownPct;
    }

    public void setTestMaxDrawDownPct(float testMaxDrawDownPct) {
        this.testMaxDrawDownPct = testMaxDrawDownPct;
    }

    public List<MonthlyReturn> getTestMonthlyReturnList() {
        return testMonthlyReturnList;
    }

    public void setTestMonthlyReturnList(List<MonthlyReturn> testMonthlyReturnList) {
        this.testMonthlyReturnList = testMonthlyReturnList;
    }

    public List<TraderTaskResult.Result.Trade> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(List<TraderTaskResult.Result.Trade> tradeHistory) {
        this.tradeHistory = tradeHistory;
    }
}