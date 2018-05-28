package io.gncloud.coin.server.model;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// ECS TASK RUN
public class Task {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Task.class);

    private Integer id; //backTestId || agentId
    private Integer strategyId;
    private String strategyVersion;
    private String options;
    private String name;
    private String userId;
    private String exchangeName;
    private float capitalBase;
    private String base;
    private String coin;
    private String state;
    private float revenue;
    private String ecsTaskId;
    private boolean live = false;
    private Date testTime;
    private boolean simulationOrder = true;
    private String version;
    private Integer exchangeKeyId;

    /** BackTest 전용 파라미터 시작 */
    private String startTime;
    private String endTime;
    private String timeInterval;
    /** BackTest 전용 파라미터 끝 */



    public Integer getExchangeKeyId() {
        return exchangeKeyId;
    }

    public void setExchangeKeyId(Integer exchangeKeyId) {
        this.exchangeKeyId = exchangeKeyId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEcsTaskId() {
        return ecsTaskId;
    }

    public void setEcsTaskId(String ecsTaskId) {
        this.ecsTaskId = ecsTaskId;
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

    public float getCapitalBase() {
        return capitalBase;
    }

    public void setCapitalBase(float capitalBase) {
        this.capitalBase = capitalBase;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
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

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRunCommand(){
        List<String> cmd = new ArrayList<>();
        cmd.add("python3");
        cmd.add("run.py");
        cmd.add(String.valueOf(this.getStrategyId()));
        cmd.add(this.getExchangeName());
        cmd.add(String.valueOf(this.getCapitalBase()));
        cmd.add(this.getBase());
        cmd.add(String.valueOf(this.isLive()));
        if(this.isLive()){
            cmd.add(String.valueOf(this.simulationOrder));
        }else{
            cmd.add(this.getStartTime());
            cmd.add(this.getEndTime());
            cmd.add(this.getTimeInterval());
        }
        logger.debug("request Parameter >> {}", cmd);
        return cmd;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", strategyId=" + strategyId +
                ", strategyVersion='" + strategyVersion + '\'' +
                ", options='" + options + '\'' +
                ", userId='" + userId + '\'' +
                ", exchangeName='" + exchangeName + '\'' +
                ", capitalBase=" + capitalBase +
                ", base='" + base + '\'' +
                ", coin='" + coin + '\'' +
                ", state='" + state + '\'' +
                ", revenue=" + revenue +
                ", ecsTaskId='" + ecsTaskId + '\'' +
                ", live=" + live +
                ", testTime=" + testTime +
                ", simulationOrder=" + simulationOrder +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeInterval='" + timeInterval + '\'' +
                ", version='" + version + '\'' +
                ", exchangeKeyId=" + exchangeKeyId +
                '}';
    }
}