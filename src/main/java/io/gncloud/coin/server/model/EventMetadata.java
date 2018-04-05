package io.gncloud.coin.server.model;

/**
 * {
     "arena": "backtest",
     "agentId": "1",
     "testId": "1",
     "starategyId": "1",
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

    private String arena;
    private String agentId;
    private String testId;
    private String starategyId;
    private String user;

    public String getArena() {
        return arena;
    }

    public void setArena(String arena) {
        this.arena = arena;
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

    public String getStarategyId() {
        return starategyId;
    }

    public void setStarategyId(String starategyId) {
        this.starategyId = starategyId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
