package io.systom.coin.model;

import java.util.List;

/*
 * create joonwoo 2018. 7. 23.
 * 
 */
public class GoodsTestResult {
    private Integer id;
    private float testReturnPct;
    private List<MonthlyReturn> testMonthlyReturnList;
    private List<TaskResult.Result.Trade> tradeHistory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getTestReturnPct() {
        return testReturnPct;
    }

    public void setTestReturnPct(float testReturnPct) {
        this.testReturnPct = testReturnPct;
    }

    public List<MonthlyReturn> getTestMonthlyReturnList() {
        return testMonthlyReturnList;
    }

    public void setTestMonthlyReturnList(List<MonthlyReturn> testMonthlyReturnList) {
        this.testMonthlyReturnList = testMonthlyReturnList;
    }

    public List<TaskResult.Result.Trade> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(List<TaskResult.Result.Trade> tradeHistory) {
        this.tradeHistory = tradeHistory;
    }
}