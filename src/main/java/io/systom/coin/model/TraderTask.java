package io.systom.coin.model;

import com.amazonaws.services.ecs.model.KeyValuePair;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 7. 16.
 * 
 */

public class TraderTask {

//    public enum SESSION_TYPE { backtest, paper, live }
    public enum ACTIONS { start, stop, backtest, reset, hide, show, error, order }

    private String id;              // 임시 아이디 발급 (signal model download 용)
    private String sessionType;
    private String action;

    private Integer strategyId;
    private Integer version;
    private String startDate;
    private String endDate;
    private String exchange;
    private String coinUnit;
    private String baseUnit;
    private String cashUnit;
    private Integer initCash;
    private String timezone;


//  ------ 라이브 전용 필드
    private Integer goodsId;
//  ------ 라이브 전용 필드

//  ------ 매뉴얼 주문
    private Float coinWeight;
    private String coinAction;
    private Float baseWeight;
    private String baseAction;
    private String message;
//  ------ 매뉴얼 주문


    public Float getCoinWeight() {
        return coinWeight;
    }

    public void setCoinWeight(Float coinWeight) {
        this.coinWeight = coinWeight;
    }

    public String getCoinAction() {
        return coinAction;
    }

    public void setCoinAction(String coinAction) {
        this.coinAction = coinAction;
    }

    public Float getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(Float baseWeight) {
        this.baseWeight = baseWeight;
    }

    public String getBaseAction() {
        return baseAction;
    }

    public void setBaseAction(String baseAction) {
        this.baseAction = baseAction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getCashUnit() {
        return cashUnit;
    }

    public void setCashUnit(String cashUnit) {
        this.cashUnit = cashUnit;
    }

    public Integer getInitCash() {
        return initCash;
    }

    public void setInitCash(Integer initCash) {
        this.initCash = initCash;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<String> getBackTestCmd() {
        return getBackTestCmd(null, null);
    }
    public List<String> getBackTestCmd(String startTime, String endTime) {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("-m");
        cmd.add("cctrader.launcher");
        cmd.add("task_id=" + this.id);
        cmd.add("session_type=" + this.sessionType);
        cmd.add("exchange_id=" + exchange);
        cmd.add("coin_unit=" + coinUnit);
        cmd.add("base_unit=" + baseUnit);
        cmd.add("cash_unit=" + cashUnit);
        cmd.add("start_date=" + startDate);
        cmd.add("end_date=" + endDate);
//        cmd.add("start_date=" + startDate + (startTime == null ? "" : " " + startTime));
//        cmd.add("end_date=" + endDate + (endTime == null ? "" : " " + endTime));
        return cmd;
    }
    public List<String> getBackTestEnv() {
        List<String> env = new ArrayList<>();
        env.add("TZ=" + this.timezone);
        env.add("LOGLEVEL=INFO");
        return env;
    }


    public List<String> getLiveSignalCmd() {
        return getLiveSignalCmd(null);
    }
    public List<String> getLiveSignalCmd(String startTime) {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("-m");
        cmd.add("cctrader.launcher");
        cmd.add("task_id=" + this.id);
        cmd.add("session_type=" + this.sessionType);
        cmd.add("exchange_id=" + this.exchange);
        cmd.add("coin_unit=" + this.coinUnit);
        cmd.add("base_unit=" + baseUnit);
        cmd.add("cash_unit=" + cashUnit);
        cmd.add("init_cash=" + this.initCash);
        cmd.add("start_date=" + startDate);
//        cmd.add("start_date=" + startDate + (startTime == null ? "" : " " + startTime));
        return cmd;
    }
    public List<KeyValuePair> getLiveSignalEnv() {
        List<KeyValuePair> env = new ArrayList<>();
        env.add(new KeyValuePair().withName("LOGLEVEL").withValue("INFO"));
        env.add(new KeyValuePair().withName("TZ").withValue(this.timezone));
        return env;
    }


    public List<String> getLiveExecutorCmd(boolean isLive) {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("-m");
        if (isLive) {
            cmd.add("cctrader.live_executor");
        } else {
            cmd.add("cctrader.mock_executor");
        }
        cmd.add("goods_id=" + this.goodsId);
        return cmd;
    }
    public List<KeyValuePair> getLiveExecutorEnv() {
        List<KeyValuePair> env = new ArrayList<>();
        env.add(new KeyValuePair().withName("LOGLEVEL").withValue("INFO"));
        env.add(new KeyValuePair().withName("TZ").withValue(this.timezone));
        return env;
    }


}