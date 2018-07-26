package io.systom.coin.model;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 7. 16.
 * 
 */

public class TraderTask {

//    public enum SESSION_TYPE { backtest, paper, live }
    public enum ACTIONS { start, stop, backtest, reset }

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

//  ------ 라이브 전용 필드
    private Integer goodsId;
//  ------ 라이브 전용 필드

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

    public List<String> getBackTestCmd() {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("launcher.py");
        cmd.add("task_id=" + this.id);
        cmd.add("session_type=" + this.sessionType);
        cmd.add("start_date=" + startDate);
        cmd.add("end_date=" + endDate);
        cmd.add("exchange_id=" + exchange);
        cmd.add("coin_unit=" + coinUnit);
        cmd.add("base_unit=" + baseUnit);
        cmd.add("cash_unit=" + cashUnit);
        return cmd;
    }

    public List<String> getLiveSignalCmd() {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("launcher.py");
        cmd.add("task_id=" + this.id);
        cmd.add("session_type=" + this.sessionType);
        cmd.add("exchange_id=" + this.exchange);
        cmd.add("start_date=" + this.startDate);
        cmd.add("coin_unit=" + this.coinUnit);
        cmd.add("base_unit=" + this.baseUnit);
        cmd.add("cash_unit=" + this.cashUnit);
        cmd.add("init_cash=" + this.initCash);
        return cmd;
    }

    public List<String> getLiveExecutorCmd() {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("mock_executor.py");
        cmd.add("goods_id=" + this.goodsId);
        return cmd;
    }

}