package io.systom.coin.model;

import java.util.List;

public class CoinSignal {
    private String type;
    private String symbol;
    private String action;
    private Float weight;
    private Reason reason;
    private Integer time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public static class Reason {
        private int target;
        private int score;
        private List<TraderTaskResult.Result.Trade.Reason.Condition> condition;
        private String author;
        private String message;

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

        public List<TraderTaskResult.Result.Trade.Reason.Condition> getCondition() {
            return condition;
        }

        public void setCondition(List<TraderTaskResult.Result.Trade.Reason.Condition> condition) {
            this.condition = condition;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Reason{" +
                    "target=" + target +
                    ", score=" + score +
                    ", condition=" + condition +
                    ", author='" + author + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
