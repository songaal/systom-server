//package io.gncloud.coin.server.model;
//
//import java.util.Date;
//
///*
// * create joonwoo 2018. 4. 13.
// *
// */
//public class Agent{
//
//    public static String STATE_STOP = "stop";
//    public static String STATE_RUN = "running";
//    public static String STATE_ERROR = "error";
//
//    private Integer id;
//    private Integer strategyId;
//    private String strategyVersion;
//    private String name;
//    private Integer exchangeKeyId;
//    private Float capitalBase;
//    private String baseCurrency;
//    private Date createTime;
//    private String options;
//    private String state;
//    private Boolean simulationOrder;
//    private String userId;
//    private String ecsTaskId;
//
//    private String strategyName;
//    private String exchangeName;
//    private String exchangeKeyName;
//
//    private String code;
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getExchangeKeyName() {
//        return exchangeKeyName;
//    }
//
//    public void setExchangeKeyName(String exchangeKeyName) {
//        this.exchangeKeyName = exchangeKeyName;
//    }
//
//    public String getExchange() {
//        return exchangeName;
//    }
//
//    public void setExchange(String exchangeName) {
//        this.exchangeName = exchangeName;
//    }
//
//    public String getStrategyName() {
//        return strategyName;
//    }
//
//    public void setStrategyName(String strategyName) {
//        this.strategyName = strategyName;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public Boolean getSimulationOrder() {
//        return simulationOrder;
//    }
//
//    public void setSimulationOrder(Boolean simulationOrder) {
//        this.simulationOrder = simulationOrder;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Integer getStrategyId() {
//        return strategyId;
//    }
//
//    public void setStrategyId(Integer strategyId) {
//        this.strategyId = strategyId;
//    }
//
//    public String getStrategyVersion() {
//        return strategyVersion;
//    }
//
//    public void setStrategyVersion(String strategyVersion) {
//        this.strategyVersion = strategyVersion;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Integer getExchangeKeyId() {
//        return exchangeKeyId;
//    }
//
//    public void setExchangeKeyId(Integer exchangeKeyId) {
//        this.exchangeKeyId = exchangeKeyId;
//    }
//
//    public Float getCapitalBase() {
//        return capitalBase;
//    }
//
//    public void setCapitalBase(Float capitalBase) {
//        this.capitalBase = capitalBase;
//    }
//
//    public String getBaseCurrency() {
//        return baseCurrency;
//    }
//
//    public void setBaseCurrency(String baseCurrency) {
//        this.baseCurrency = baseCurrency;
//    }
//
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//
//    public String getOptions() {
//        return options;
//    }
//
//    public void setOptions(String options) {
//        this.options = options;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getEcsTaskId() {
//        return ecsTaskId;
//    }
//
//    public void setEcsTaskId(String ecsTaskId) {
//        this.ecsTaskId = ecsTaskId;
//    }
//
//    public Task cloneTask() {
//        Task task = new Task();
//        task.setId(this.id);
//        task.setStrategyId(this.getStrategyId());
////        task.setBase(this.baseCurrency);
//        task.setInitialBase(this.capitalBase);
//        return task;
//    }
//}