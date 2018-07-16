package io.systom.coin.model;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 7. 16.
 * 
 */

public class Task {

    private String id;

    private Integer goodsId;
    private Integer strategyId;
    private Integer version;

    private String startDate;
    private String endDate;
    private String exchange;
    private String coinUnit;
    private String baseUnit;
    private String cashUnit;

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

    public List<String> getDockerRunCommand() {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("downloader.py");
        cmd.add("startDate=" + startDate);
        cmd.add("endDate=" + endDate);
        cmd.add("exchange=" + exchange);
        cmd.add("coinUnit=" + coinUnit);
        cmd.add("baseUnit=" + baseUnit);
        cmd.add("cashUnit=" + cashUnit);
        cmd.add("endDate=" + endDate);
        return cmd;
    }

    public List<String> getDockerRunEnv() {
        List<String> env = new ArrayList<>();
        env.add(String.format("TASK_ID=%s", this.id));
        return env;
    }

}