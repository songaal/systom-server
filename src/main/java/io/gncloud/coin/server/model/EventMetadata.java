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
    private String agentId;
    private String testId;
    private String strategyId;
    private String user;
    private String progress; //진행상태: running, finished
    private List<Order> orders;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
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

    /*
    * {
         "id": "b00597cd281e41f2b9f5b2aa9ec0362b",
         "timestamp": 1515556800000000000,
         "exchange": "poloniex",
         "coin": "btc",
         "base": "bts",
         "amount": 1,
         "price": 0.00004305,
         "fee": 0
         }
    * */
    public static class Order {
        private String id;
        private Long timestamp;
        private String exchange;
        private String coin;
        private String base;
        private Integer amount;
        private Double price;
        private Double fee;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
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

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Double getFee() {
            return fee;
        }

        public void setFee(Double fee) {
            this.fee = fee;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "id='" + id + '\'' +
                    ", timestamp=" + timestamp +
                    ", exchange='" + exchange + '\'' +
                    ", coin='" + coin + '\'' +
                    ", base='" + base + '\'' +
                    ", amount=" + amount +
                    ", price=" + price +
                    ", fee=" + fee +
                    '}';
        }
    }
}
