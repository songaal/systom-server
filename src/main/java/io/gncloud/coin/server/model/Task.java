package io.gncloud.coin.server.model;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// ECS TASK RUN
public class Task {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Task.class);

    private String id;
    private String name;
    private String strategyId;
    private String strategyVersion;
    private String options;
    private String userId;

    private String exchangeName;
    private float startMoney;
    private String currency;
    private String coin;

    private String state;
    private float revenue;

    private String ecsTask;
    private boolean live = false;

    private boolean simulationOrder = true;
    private String startTime;
    private String endTime;
    private String dataFrequency;

    public String getEcsTask() {
        return ecsTask;
    }

    public void setEcsTask(String ecsTask) {
        this.ecsTask = ecsTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyVersion() {
        return strategyVersion;
    }

    public void setStrategyVersion(String strategyVersion) {
        this.strategyVersion = strategyVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public float getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(float startMoney) {
        this.startMoney = startMoney;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isSimulationOrder() {
        return simulationOrder;
    }

    public void setSimulationOrder(boolean simulationOrder) {
        this.simulationOrder = simulationOrder;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDataFrequency() {
        return dataFrequency;
    }

    public void setDataFrequency(String dataFrequency) {
        this.dataFrequency = dataFrequency;
    }

    public List<String> runCommand(){
        List<String> cmd = new ArrayList<>();
        cmd.add("python3");
        cmd.add("run.py");
        cmd.add(this.getStrategyId());
        cmd.add(this.getExchangeName());
        cmd.add(this.getCurrency());
        cmd.add(String.valueOf(this.getStartMoney()));
        cmd.add(String.valueOf(this.isLive()));
        if(this.isLive()){
            cmd.add(String.valueOf(this.simulationOrder));
        }else{
            cmd.add(this.getStartTime());
            cmd.add(this.getEndTime());
            cmd.add(this.getDataFrequency());
        }
        logger.debug("request Parameter >> {}", cmd);
        return cmd;
    }
}