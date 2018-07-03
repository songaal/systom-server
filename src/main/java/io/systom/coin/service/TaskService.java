package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.TaskResult;
import io.systom.coin.model.Strategy;
import io.systom.coin.model.Task;
import io.systom.coin.utils.DockerUtils;
import io.systom.coin.utils.TaskFuture;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

@Service
public class TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static Logger backTestLogger = LoggerFactory.getLogger("backTestLogger");

    @Value("${backtest.resultTimeout}")
    private long resultTimeout;

    @Value("${backtest.capitalBase}")
    private float capitalBase;

    @Autowired private SqlSession sqlSession;
    @Autowired private DockerUtils dockerUtils;

//    @Autowired private EcsUtils ecsUtils;
//    @Autowired private IdentityService identityService;
//    @Autowired private StrategyService oldStrategyService;
//    @Autowired private ExchangeService exchangeService;
//    @Autowired private AgentService agentService;

    private static ConcurrentMap<String, TaskFuture> backTestResult = new ConcurrentHashMap<>();

    public Map<String, Object> syncBackTest(Task task) throws TimeoutException, ParameterException, OperationException {
        task.setInitialBase(capitalBase);

        isNotEmpty(task.getStrategyId(), "strategyId");
        isNotEmpty(task.getExchange(), "exchange");
        isNotEmpty(task.getSymbol(), "symbol");
        isNotEmpty(task.getTimeInterval(), "timeInterval");
        isNotEmpty(task.getStartTime(), "start");
        isNotEmpty(task.getEndTime(), "end");

        try {
            logger.debug("[ BACK TEST ] RUN {}", task);

            task.setId(UUID.randomUUID().toString());

            logger.info("Generator Task Id: {}", task.getId());

            dockerUtils.syncRun(task.getId(), task.getRunEnv(), task.getRunCommand());

            TaskFuture<Map<String, Object>> future = backTestResult.get(task.getId());
            Map<String, Object> resultJson = future.take();
            backTestResult.remove(task.getId());
            if (resultJson != null) {
                backTestLogger.info("[{}] BackTest result catch!", task.getId());
            } else {
                backTestLogger.info("[{}] BackTest Response Timeout Error.", task.getId());
                throw new TimeoutException("[" + task.getId() + "] BackTest Response Timeout Error.");
            }
            backTestLogger.info("[{}] BackTest Successful.", task.getId());
            return resultJson;
        } catch (Throwable t) {
            logger.error("", t);
            throw new OperationException("[FAIL] Running BackTest.");
        }
    }

    public Map<String, Object> registerBackTestResult(String id, Map<String, Object> resultJson) {
        TaskFuture<Map<String, Object>> taskFuture = new TaskFuture();
        taskFuture.offer(resultJson);
        backTestResult.put(id, taskFuture);
        backTestLogger.debug("[{}] BackTest Result Saved.", id);
        return resultJson;
    }

    public Strategy getBackTestModel(String testId, String userId, Integer version) throws ParameterException, OperationException {
        Task task = new Task();
        task.setId(testId);
        task.setUserId(userId);
        task.setVersion(version);
        try {
            return sqlSession.selectOne("backtest.getModel", task);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Update Failed testId: " + testId);

        }
    }

    protected void recordBackTestPerformance(int investGoodsId, TaskResult.Result result) {
        try {
            result.setId(investGoodsId);
            int changeRow = sqlSession.insert("backtest.recordPerformance", result);
            logger.debug("recordPerformance row: {}", changeRow);
        } catch (Exception e) {

        }
    }

    protected void recordBackTestTradeHistory(int investGoodsId, List<TaskResult.Result.Trade> trades) {
        trades.forEach(trade -> {
            trade.setId(investGoodsId);
        });
        int changeRow = sqlSession.insert("backtest.recordTradeHistory", trades);
        logger.debug("recordTradeHistory row: {}", changeRow);
    }

    protected void recordBackTestValueHistory(int investGoodsId, Map<Long, Float> equities, Map<Long, Float> cumReturns, Map<Long, Float> drawdowns){
        List<TaskResult.Result.Value> values = new ArrayList<>();
        Iterator<Map.Entry<Long, Float>> iterator = equities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            TaskResult.Result.Value v = new TaskResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setEquity(entry.getValue());
            if (cumReturns.get(ts) != null) {
                v.setCumReturn(cumReturns.get(ts));
                cumReturns.remove(ts);
            }
            if (drawdowns.get(ts) != null) {
                v.setDrawdown(drawdowns.get(ts));
                drawdowns.remove(ts);
            }
            values.add(v);
        }

        iterator = cumReturns.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            TaskResult.Result.Value v = new TaskResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setCumReturn(cumReturns.get(ts));
            if (drawdowns.get(ts) != null) {
                v.setDrawdown(drawdowns.get(ts));
                drawdowns.remove(ts);
            }
            values.add(v);
        }

        iterator = drawdowns.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            TaskResult.Result.Value v = new TaskResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setDrawdown(drawdowns.get(ts));
            values.add(v);
        }
        int changeRow = sqlSession.insert("backtest.recordValueHistory", values);
        logger.debug("recordValueHistory row: {}", changeRow);
    }





    public ConcurrentMap<String, TaskFuture> getBackTestResult() {
        return this.backTestResult;
    }


//    public Task runAgentTask(String userId, String accessToken, Integer agentId, boolean isLiveMode) throws ParameterException, OperationException {
////        TODO 에이전트 cctrader 변경
//        throw new OperationException("개발예정");
////
////        Task task = getAgentTaskFromId(agentId);
////
////        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(task.getExchangeKeyId(), userId));
////        RunBackTestRequest.ExchangeAuth exchangeAuth = null;
////        String exchangeName = exchangeKey.getExchange();
////
////        List<KeyValuePair> environmentList = new ArrayList<>();
////        if(isLiveMode) {
////            environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(exchangeAuth.getKey()));
////            environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(exchangeAuth.getSecret()));
////        }
////        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));
////        environmentList.add(new KeyValuePair().withName("user_id").withValue(userId));
////        environmentList.add(new KeyValuePair().withName("access_token").withValue(accessToken));
////        environmentList.add(new KeyValuePair().withName("agent_id").withValue(String.valueOf(agentId)));
////
////        logger.debug("[ LIVE={} ] RUN {}", isLiveMode, task);
////        RunTaskResult result = null;
////        try {
////            result = ecsUtils.runTask(task, environmentList);
////        } catch (Exception e){
////            logger.error("", e);
////            throw new OperationException("[FAIL] ecs task syncRun error");
////        }
////
////        String ecsTaskId = parseTaskId(result);
////        task.setEcsTaskId(ecsTaskId);
////        logger.info("starting ecs: {}", ecsTaskId);
////        Agent agent = new Agent();
////        agent.setId(task.getId());
////        agent.setEcsTaskId(ecsTaskId);
////        agent.setState(Agent.STATE_RUN);
////        agentService.updateAgent(agent);
////        return task;
//    }
//
//
//    public Task stopAgentTask(String userId, Integer agentId) throws ParameterException, OperationException {
////        TODO 에이전트 정지
//        throw new OperationException("개발예정");
////        Agent agent = agentService.getAgent(agentId);
////        Task task = new Task();
////        task.setEcsTaskId(agent.getEcsTaskId());
////        StopTaskResult stopTaskResult = null;
////        try {
////            stopTaskResult = ecsUtils.stopTask(task.getEcsTaskId(), "User stop request : " + userId + ", " + agentId);
////        } catch (Exception e){
////            logger.error("", e);
////            throw new OperationException("[FAIL] ecs task syncRun error");
////        }
////        com.amazonaws.services.ecs.model.Task stopedTask = stopTaskResult.getTask();
////        logger.debug("Stopped task : {}", stopedTask);
////        agent.setState(Agent.STATE_STOP);
////        agentService.updateAgent(agent);
////        return task;
//
//    }
//
//
//    private String parseTaskId(RunTaskResult result) {
//        return result.getTasks().get(0).getTaskArn().split("/")[1];
//    }
//
//    private Task getAgentTaskFromId(Integer agentId) throws ParameterException, OperationException {
//
//        //agent 테이블을 읽어서 Task에 채워준다.
//        Agent agent = agentService.getAgent(agentId);
//        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(agent.getExchangeKeyId(), agent.getUserId()));
//        Task task = agent.cloneTask();
//        task.setExchange(exchangeKey.getExchange());
//        task.setExchangeKeyId(agent.getExchangeKeyId());
//        return task;
//    }
//
//
//    public List<Task> getBackTestHistory(String strategyId) throws OperationException {
//        try {
//            return sqlSession.selectList("backtest.selectHistory", strategyId);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Test History");
//        }
//    }
//
//    public void deleteBackTestHistory(OldStrategy oldStrategy) throws OperationException {
//        try {
//            sqlSession.delete("backtest.deleteTestHistory", oldStrategy);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Test History");
//        }
//    }
//


    private void isNotEmpty(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotEmpty(Integer field, String label) throws ParameterException {
        if(field == null){
            throw new ParameterException(label);
        }
    }
    private void isNotZero(float field, String label) throws ParameterException {
        if(field == 0.0f){
            throw new ParameterException(label);
        }
    }
}