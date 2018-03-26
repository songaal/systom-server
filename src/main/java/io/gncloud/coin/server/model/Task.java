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

    private String taskId;
    private String userId;
    private String exchangeName;
    private String baseCurrency;
    private float capitalBase;
    private boolean live = false;
    private boolean simulationOrder = true;
    private String start;
    private String end;
    private String dataFrequency;
    private String algoId;

    public String getAlgoId() {
        return algoId;
    }

    public void setAlgoId(String algoId) {
        this.algoId = algoId;
    }

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

    public List<String> runCommand(){
        List<String> cmd = new ArrayList<>();
        cmd.add("python3");
        cmd.add("run.py");
        cmd.add(this.getAlgoId());
        cmd.add(this.getExchangeName());
        cmd.add(this.getBaseCurrency());
        cmd.add(String.valueOf(this.getCapitalBase()));
        cmd.add(String.valueOf(this.isLive()));
        if(this.isLive()){
            cmd.add(String.valueOf(this.simulationOrder));
        }else{
            cmd.add(this.getStart());
            cmd.add(this.getEnd());
            cmd.add(this.getDataFrequency());
        }
        logger.debug("request Parameter >> {}", cmd);
        return cmd;
    }
}