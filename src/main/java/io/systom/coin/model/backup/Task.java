package io.systom.coin.model.backup;

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

    public enum SESSION_TYPES { backtest, paper, live }

    private String launcher_name;
    private String id;
    private Integer strategyId;
    private Integer version;
    private String userId;
    private String exchange;
    private float initialBase = 1.0f;
    private float initialCash = 0;
    private float initialCoin = 0;
    private float commissionRate = 0.001f;
    private String benchmark_symbol = "btc_usdt";
    private String symbol;
    private String sessionType;
    private String state;
    private Date testTime;
    private Integer exchangeKeyId;
    private String accessToken;
    private String algoClassName = "Main";

    /** BackTest 전용 파라미터 시작 */
    private String startTime;
    private String endTime;
    private String timeInterval;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** BackTest 전용 파라미터 끝 */


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExchangeKeyId() {
        return exchangeKeyId;
    }

    public void setExchangeKeyId(Integer exchangeKeyId) {
        this.exchangeKeyId = exchangeKeyId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public float getInitialBase() {
        return initialBase;
    }

    public void setInitialBase(float initialBase) {
        this.initialBase = initialBase;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public float getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(float initialCash) {
        this.initialCash = initialCash;
    }

    public float getInitialCoin() {
        return initialCoin;
    }

    public void setInitialCoin(float initialCoin) {
        this.initialCoin = initialCoin;
    }

    public float getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(float commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getBenchmark_symbol() {
        return benchmark_symbol;
    }

    public void setBenchmark_symbol(String benchmark_symbol) {
        this.benchmark_symbol = benchmark_symbol;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getAlgoClassName() {
        return algoClassName;
    }

    public void setAlgoClassName(String algoClassName) {
        this.algoClassName = algoClassName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLauncher_name() {
        return launcher_name;
    }

    public void setLauncher_name(String launcher_name) {
        this.launcher_name = launcher_name;
    }

    public List<String> getRunEnv(){
        List<String> env = new ArrayList<>();
        env.add("benchmark_symbol=" + this.getBenchmark_symbol());
        env.add("algo_class_name=" + this.getAlgoClassName());
        env.add("exchange=" + this.getExchange());
        env.add("access_token=" + this.getAccessToken());
        logger.debug("task env: {}", env);
        return env;
    }

    public List<String> getRunCommand(){
        List<String> cmd = new ArrayList<>();
        cmd.add("python3");
        cmd.add("syncRun.py");
        cmd.add("task_id=" + this.getId());
        cmd.add("initial_cash=" + String.valueOf(this.getInitialCash()));
        cmd.add("initial_base=" + String.valueOf(this.getInitialBase()));
        cmd.add("initial_coin=" + String.valueOf(this.getInitialCoin()));
        cmd.add("symbol=" + this.getSymbol());
        cmd.add("start=" + this.getStartTime());
        cmd.add("end=" + this.getEndTime());
        cmd.add("interval=" + this.getTimeInterval());
        cmd.add("session_type=" + this.getSessionType());
        cmd.add("algo_class_name=" + this.getAlgoClassName());
        logger.debug("task cmd: {}", cmd);
        return cmd;
    }

    @Override
    public String toString() {
        return "TraderTask{" +
                "id=" + id +
                ", strategyId=" + strategyId +
                ", userId='" + userId + '\'' +
                ", exchange='" + exchange + '\'' +
                ", initialBase=" + initialBase +
                ", initialCash=" + initialCash +
                ", initialCoin=" + initialCoin +
                ", commissionRate=" + commissionRate +
                ", benchmark_symbol='" + benchmark_symbol + '\'' +
                ", symbol='" + symbol + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", state='" + state + '\'' +
                ", testTime=" + testTime +
                ", exchangeKeyId=" + exchangeKeyId +
                ", accessToken='" + accessToken + '\'' +
                ", algoClassName='" + algoClassName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeInterval='" + timeInterval + '\'' +
                '}';
    }
}