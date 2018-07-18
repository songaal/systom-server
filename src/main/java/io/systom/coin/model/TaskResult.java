package io.systom.coin.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
public class TaskResult {

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

    public static class Request {
        private String start;
        private String end;
        private int day;
        private String symbol;
        private String exchange;
        @SerializedName("session_type")
        private String sessionType;

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

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
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
                    ", day=" + day +
                    ", symbol='" + symbol + '\'' +
                    ", exchange='" + exchange + '\'' +
                    ", sessionType='" + sessionType + '\'' +
                    '}';
        }
    }


    public static class Result {
//        private Integer id;
        @SerializedName("total_equity")
        private float totalEquity;
        @SerializedName("total_equity_usd")
        private float totalEquityUsd;
        @SerializedName("total_commission")
        private float totalCommission;
        @SerializedName("base_price")
        private float basePrice;
        @SerializedName("return_pct")
        private float returnPct;
        @SerializedName("maxReturn_pct")
        private float maxReturnPct;
        private int trades;
        @SerializedName("wins_pct")
        private float winsPct;
        @SerializedName("wins_count")
        private int winsCount;
        @SerializedName("lose_count")
        private int loseCount;
        @SerializedName("pnl_rate")
        private float pnlRate;
        @SerializedName("max_drawdown")
        private float maxDrawdown;
        @SerializedName("max_drawdown_duration")
        private float maxDrawdownDuration;
        @SerializedName("wins_return_avg")
        private float winsReturnAvg;
        @SerializedName("lose_return_avg")
        private float loseReturnAvg;
        @SerializedName("trade_history")
        private List<Trade> tradeHistory;
        @SerializedName("cum_returns")
        private Map<String, Float> cumReturns;
        private Map<String, Float> drawdowns;
        private Map<String, Float> equity;


        public float getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(float basePrice) {
            this.basePrice = basePrice;
        }

        public float getWinsPct() {
            return winsPct;
        }

        public void setWinsPct(float winsPct) {
            this.winsPct = winsPct;
        }

        public float getTotalEquity() {
            return totalEquity;
        }

        public void setTotalEquity(float totalEquity) {
            this.totalEquity = totalEquity;
        }

        public float getTotalEquityUsd() {
            return totalEquityUsd;
        }

        public void setTotalEquityUsd(float totalEquityUsd) {
            this.totalEquityUsd = totalEquityUsd;
        }

        public float getTotalCommission() {
            return totalCommission;
        }

        public void setTotalCommission(float totalCommission) {
            this.totalCommission = totalCommission;
        }

        public float getReturnPct() {
            return returnPct;
        }

        public void setReturnPct(float returnPct) {
            this.returnPct = returnPct;
        }

        public float getMaxReturnPct() {
            return maxReturnPct;
        }

        public void setMaxReturnPct(float maxReturnPct) {
            this.maxReturnPct = maxReturnPct;
        }

        public List<Trade> getTradeHistory() {
            return tradeHistory;
        }

        public void setTradeHistory(List<Trade> tradeHistory) {
            this.tradeHistory = tradeHistory;
        }

        public int getTrades() {
            return trades;
        }

        public void setTrades(int trades) {
            this.trades = trades;
        }

        public int getWinsCount() {
            return winsCount;
        }

        public void setWinsCount(int winsCount) {
            this.winsCount = winsCount;
        }

        public int getLoseCount() {
            return loseCount;
        }

        public void setLoseCount(int loseCount) {
            this.loseCount = loseCount;
        }

        public float getPnlRate() {
            return pnlRate;
        }

        public void setPnlRate(float pnlRate) {
            this.pnlRate = pnlRate;
        }

        public float getMaxDrawdown() {
            return maxDrawdown;
        }

        public void setMaxDrawdown(float maxDrawdown) {
            this.maxDrawdown = maxDrawdown;
        }

        public float getMaxDrawdownDuration() {
            return maxDrawdownDuration;
        }

        public void setMaxDrawdownDuration(float maxDrawdownDuration) {
            this.maxDrawdownDuration = maxDrawdownDuration;
        }

        public float getWinsReturnAvg() {
            return winsReturnAvg;
        }

        public void setWinsReturnAvg(float winsReturnAvg) {
            this.winsReturnAvg = winsReturnAvg;
        }

        public float getLoseReturnAvg() {
            return loseReturnAvg;
        }

        public void setLoseReturnAvg(float loseReturnAvg) {
            this.loseReturnAvg = loseReturnAvg;
        }

        public Map<String, Float> getCumReturns() {
            return cumReturns;
        }

        public void setCumReturns(Map<String, Float> cumReturns) {
            this.cumReturns = cumReturns;
        }

        public Map<String, Float> getDrawdowns() {
            return drawdowns;
        }

        public void setDrawdowns(Map<String, Float> drawdowns) {
            this.drawdowns = drawdowns;
        }

        public Map<String, Float> getEquity() {
            return equity;
        }

        public void setEquity(Map<String, Float> equity) {
            this.equity = equity;
        }

        public static class Trade {
            private Integer id; //investId
            private String time;
            private String symbol;
            private String action;
            private String exchange;
            private float quantity;
            private float price;
            private float commission;
            private Reason reason;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public float getQuantity() {
                return quantity;
            }

            public void setQuantity(float quantity) {
                this.quantity = quantity;
            }

            public String getExchange() {
                return exchange;
            }

            public void setExchange(String exchange) {
                this.exchange = exchange;
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

            public String getReason() {
                return reason.toJson();
            }

            public void setReason(Reason reason) {
                this.reason = reason;
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

        public static class Value {
            private Integer id;
            private Long timestamp;
            private Float cumReturn;
            private Float drawdown;
            private Float equity;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Long timestamp) {
                this.timestamp = timestamp;
            }

            public Float getCumReturn() {
                return cumReturn;
            }

            public void setCumReturn(Float cumReturn) {
                this.cumReturn = cumReturn;
            }

            public Float getDrawdown() {
                return drawdown;
            }

            public void setDrawdown(Float drawdown) {
                this.drawdown = drawdown;
            }

            public Float getEquity() {
                return equity;
            }

            public void setEquity(Float equity) {
                this.equity = equity;
            }
        }// end value

    }// end result



}// end backTestResult
