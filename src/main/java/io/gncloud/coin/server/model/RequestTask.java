package io.gncloud.coin.server.model;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */

// ECS TASK RUN
public class RequestTask {

    private String taskId;
    private String userId;
    private String exchangeName;
    private String baseCurrency;
    private float capitalBase;
    private boolean live;
    private boolean simulationOrder;
    private String start;
    private String end;
    private String dataFrequency;



    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public float getCapitalBase() {
        return capitalBase;
    }

    public void setCapitalBase(float capitalBase) {
        this.capitalBase = capitalBase;
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

    public String getDataFrequency() {
        return dataFrequency;
    }

    public void setDataFrequency(String dataFrequency) {
        this.dataFrequency = dataFrequency;
    }
}