package io.gncloud.coin.server.model;

import java.util.List;

/**
 * {
     "mode": "backtest",
     "agentId": "1",
     "testId": "1",
     "strategyId": "1",
     "user": "testuser",
     "price": {
         "timestamp": 1515556800000000000,
         "exchange": "poloniex",
         "coin": "bts",
         "price": 0.00004305,
         "base": "btc",
         "indicator": {
         "price": 0.00004517125000000001,
         "short_mavg": 0.00004517125000000001,
         "long_mavg": 0.00004729294117647058
         }
     },
     "orders": [
         {
         "id": "b00597cd281e41f2b9f5b2aa9ec0362b",
         "timestamp": 1515556800000000000,
         "exchange": "poloniex",
         "coin": "btc",
         "base": "bts",
         "amount": 1,
         "price": 0.00004305,
         "fee": 0
         }
     ]
 }
 *
 * */
public class EventMetadata {

    private static final String BACKTEST_MODE = "backtest";
    private static final String LIVE_MODE = "live";
    private static final String PAPER_MODE = "paper";

    private static final String PROGRESS_FINISHED = "finished";
    private static final String PROGRESS_RUNNING = "running";

    private String mode;
    private Integer agentId;
    private Integer testId;
    private Integer strategyId;
    private String user;
    private String progress; //진행상태: running, finished
    private List<Order> orders;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isBackTestMode() {
        return BACKTEST_MODE.equals(mode);
    }

    public boolean isLiveMode() {
        return LIVE_MODE.equals(mode);
    }

    public boolean isPaperMode() {
        return PAPER_MODE.equals(mode);
    }

    public boolean isRunning() {
        return PROGRESS_RUNNING.equals(progress);
    }

    public boolean isFinished() {
        return PROGRESS_FINISHED.equals(progress);
    }


}
