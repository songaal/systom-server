package io.systom.coin.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
public class TraderTaskResult {

    private String status;
    private String message;
    private int time;
    private Request request;
    private Result result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Request{
        @SerializedName("session_type")
        private String sessionType;
        private String start;
        private String end;
        private int days;
        private String symbol;
        private String exchange;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getSessionType() {
            return sessionType;
        }

        public void setSessionType(String sessionType) {
            this.sessionType = sessionType;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "start='" + start + '\'' +
                    ", end='" + end + '\'' +
                    ", days=" + days +
                    ", symbol='" + symbol + '\'' +
                    ", exchange='" + exchange + '\'' +
                    ", sessionType='" + sessionType + '\'' +
                    '}';
        }
    }


    public static class Result{
        private Integer id; //invest id

        @SerializedName("portfolio_stat")
        private PortfolioStat portfolioStat;
        private Map<String, Float> equity;
        private Map<String, Float> drawdowns;
        @SerializedName("cum_returns")
        private Map<String, Float> cumReturns;
        @SerializedName("monthly_cum_returns")
        private Map<String, Float> monthlyCumReturns;
        @SerializedName("max_drawdown_pct")
        private float maxDrawdownPct;
        @SerializedName("max_drawdown_duration")
        private float maxDrawdownDuration;
        @SerializedName("returns_pct")
        private float returnsPct;
        @SerializedName("max_returns_pct")
        private float maxReturnsPct;
        @SerializedName("trade_stat")
        private TradeStat tradeStat;
        @SerializedName("trade_history")
        private List<Trade> tradeHistory;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public PortfolioStat getPortfolioStat() {
            return portfolioStat;
        }

        public void setPortfolioStat(PortfolioStat portfolioStat) {
            this.portfolioStat = portfolioStat;
        }

        public Map<String, Float> getEquity() {
            return equity;
        }

        public void setEquity(Map<String, Float> equity) {
            this.equity = equity;
        }

        public Map<String, Float> getDrawdowns() {
            return drawdowns;
        }

        public void setDrawdowns(Map<String, Float> drawdowns) {
            this.drawdowns = drawdowns;
        }

        public Map<String, Float> getCumReturns() {
            return cumReturns;
        }

        public void setCumReturns(Map<String, Float> cumReturns) {
            this.cumReturns = cumReturns;
        }

        public float getMaxDrawdownPct() {
            return maxDrawdownPct;
        }

        public void setMaxDrawdownPct(float maxDrawdownPct) {
            this.maxDrawdownPct = maxDrawdownPct;
        }

        public float getMaxDrawdownDuration() {
            return maxDrawdownDuration;
        }

        public void setMaxDrawdownDuration(float maxDrawdownDuration) {
            this.maxDrawdownDuration = maxDrawdownDuration;
        }

        public Map<String, Float> getMonthlyCumReturns() {
            return monthlyCumReturns;
        }

        public void setMonthlyCumReturns(Map<String, Float> monthlyCumReturns) {
            this.monthlyCumReturns = monthlyCumReturns;
        }

        public float getReturnsPct() {
            return returnsPct;
        }

        public void setReturnsPct(float returnsPct) {
            this.returnsPct = returnsPct;
        }

        public float getMaxReturnsPct() {
            return maxReturnsPct;
        }

        public void setMaxReturnsPct(float maxReturnsPct) {
            this.maxReturnsPct = maxReturnsPct;
        }

        public TradeStat getTradeStat() {
            return tradeStat;
        }

        public void setTradeStat(TradeStat tradeStat) {
            this.tradeStat = tradeStat;
        }

        public List<Trade> getTradeHistory() {
            return tradeHistory;
        }

        public void setTradeHistory(List<Trade> tradeHistory) {
            this.tradeHistory = tradeHistory;
        }

        public static class PortfolioStat {
            @SerializedName("init_cash")
            private float initCash;
            @SerializedName("cur_cash")
            private float curCash;
            @SerializedName("cash_unit")
            private String cashUnit;
            @SerializedName("base_symbol")
            private String baseSymbol;
            private float equity;
            private String commission;
            @SerializedName("coin_invested")
            private boolean coinInvested;
            @SerializedName("base_invested")
            private boolean baseInvested;
            private String positions;
            @SerializedName("update_time")
            private long updateTime;

            public float getInitCash() {
                return initCash;
            }

            public void setInitCash(float initCash) {
                this.initCash = initCash;
            }

            public float getCurCash() {
                return curCash;
            }

            public void setCurCash(float curCash) {
                this.curCash = curCash;
            }

            public String getCashUnit() {
                return cashUnit;
            }

            public void setCashUnit(String cashUnit) {
                this.cashUnit = cashUnit;
            }

            public String getBaseSymbol() {
                return baseSymbol;
            }

            public void setBaseSymbol(String baseSymbol) {
                this.baseSymbol = baseSymbol;
            }

            public float getEquity() {
                return equity;
            }

            public void setEquity(float equity) {
                this.equity = equity;
            }

            public String getCommission() {
                return commission;
            }

            public void setCommission(String commission) {
                this.commission = commission;
            }

            public boolean isCoinInvested() {
                return coinInvested;
            }

            public void setCoinInvested(boolean coinInvested) {
                this.coinInvested = coinInvested;
            }

            public boolean isBaseInvested() {
                return baseInvested;
            }

            public void setBaseInvested(boolean baseInvested) {
                this.baseInvested = baseInvested;
            }

            public String getPositions() {
                return positions;
            }

            public void setPositions(String positions) {
                this.positions = positions;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }// PortfolioStat end

        public static class TradeStat {
            @SerializedName("trade_count")
            private int tradeCount;
            @SerializedName("win_count")
            private int winCount;
            @SerializedName("lose_count")
            private int loseCount;
            @SerializedName("win_rate")
            private float winRate;
            @SerializedName("profit_rate_sum")
            private float profitRateSum;
            @SerializedName("profit_rate_avg")
            private float profitRateAvg;
            @SerializedName("loss_rate_sum")
            private float lossRateSum;
            @SerializedName("loss_rate_avg")
            private float lossRateAvg;
            @SerializedName("pnl_rate")
            private float pnlRate;
            @SerializedName("update_time")
            private long updateTime;

            public int getTradeCount() {
                return tradeCount;
            }

            public void setTradeCount(int tradeCount) {
                this.tradeCount = tradeCount;
            }

            public int getWinCount() {
                return winCount;
            }

            public void setWinCount(int winCount) {
                this.winCount = winCount;
            }

            public int getLoseCount() {
                return loseCount;
            }

            public void setLoseCount(int loseCount) {
                this.loseCount = loseCount;
            }

            public float getWinRate() {
                return winRate;
            }

            public void setWinRate(float winRate) {
                this.winRate = winRate;
            }

            public float getProfitRateSum() {
                return profitRateSum;
            }

            public void setProfitRateSum(float profitRateSum) {
                this.profitRateSum = profitRateSum;
            }

            public float getProfitRateAvg() {
                return profitRateAvg;
            }

            public void setProfitRateAvg(float profitRateAvg) {
                this.profitRateAvg = profitRateAvg;
            }

            public float getLossRateSum() {
                return lossRateSum;
            }

            public void setLossRateSum(float lossRateSum) {
                this.lossRateSum = lossRateSum;
            }

            public float getLossRateAvg() {
                return lossRateAvg;
            }

            public void setLossRateAvg(float lossRateAvg) {
                this.lossRateAvg = lossRateAvg;
            }

            public float getPnlRate() {
                return pnlRate;
            }

            public void setPnlRate(float pnlRate) {
                this.pnlRate = pnlRate;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        } // TradeStat end


        public static class Trade {
            private Integer id; //investId
            @SerializedName("trade_time")
            private String tradeTime;
            private String action;
            private String symbol;
            private float quantity;
            private float price;
            private float commission;
            @SerializedName("commission_unit")
            private String commissionUnit;
            private Reason reason;
            private float pnl;
            @SerializedName("pnl_rate")
            private float pnlRate;
            @SerializedName("avg_price")
            private float avgPrice;

            public String getCommissionUnit() {
                return commissionUnit;
            }

            public void setCommissionUnit(String commissionUnit) {
                this.commissionUnit = commissionUnit;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getTradeTime() {
                return tradeTime;
            }

            public void setTradeTime(String tradeTime) {
                this.tradeTime = tradeTime;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public float getQuantity() {
                return quantity;
            }

            public void setQuantity(float quantity) {
                this.quantity = quantity;
            }

            public float getPrice() {
                return price;
            }

            public void setPrice(float price) {
                this.price = price;
            }

            public float getCommission() {
                return commission;
            }

            public void setCommission(float commission) {
                this.commission = commission;
            }

            public float getPnl() {
                return pnl;
            }

            public void setPnl(float pnl) {
                this.pnl = pnl;
            }

            public float getPnlRate() {
                return pnlRate;
            }

            public void setPnlRate(float pnlRate) {
                this.pnlRate = pnlRate;
            }

            public float getAvgPrice() {
                return avgPrice;
            }

            public void setAvgPrice(float avgPrice) {
                this.avgPrice = avgPrice;
            }

            public String getReason() {
                if (reason == null) {
                    return new Gson().toJson(new Reason());
                } else {
                    return reason.toJson();
                }
            }

            public void setReason(Reason reason) {
                this.reason = reason;
            }

            public void setStrReason(String reason) {
                this.reason = new Gson().fromJson(reason, Reason.class);
            }

            public static class Reason {
                private int target;
                private int score;
                private List<Condition> condition;

                public int getTarget() {
                    return target;
                }

                public void setTarget(int target) {
                    this.target = target;
                }

                public int getScore() {
                    return score;
                }

                public void setScore(int score) {
                    this.score = score;
                }

                public List<Condition> getCondition() {
                    return condition;
                }

                public void setCondition(List<Condition> condition) {
                    this.condition = condition;
                }

                public static class Condition {
                    private String name;
                    private String value;
                    private int score;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public int getScore() {
                        return score;
                    }

                    public void setScore(int score) {
                        this.score = score;
                    }
                }

                public String toJson() {
                    return new Gson().toJson(this);
                }
            }// end reason
        }// end trade

    }// end result

    @Override
    public String toString() {
        return "TraderTaskResult{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", request=" + request +
                ", result=" + result +
                '}';
    }
}// end backTestResult
