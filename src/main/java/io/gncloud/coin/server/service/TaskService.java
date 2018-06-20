package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.RunTaskResult;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.model.ExchangeKey;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.utils.DockerUtils;
import io.gncloud.coin.server.utils.EcsUtils;
import io.gncloud.coin.server.utils.TaskFuture;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private EcsUtils ecsUtils;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DockerUtils dockerUtils;

    private static ConcurrentMap<Integer, TaskFuture> backTestResult = new ConcurrentHashMap<>();

    public Map<String, Object> runAndWaitBackTestTask(Task task) throws InterruptedException, TimeoutException, ParameterException, AuthenticationException, OperationException {
        task.setInitialBase(capitalBase);
        isNotEmpty(task.getStrategyId(), "strategyId");
        isNotEmpty(task.getExchangeName(), "exchange");
        isNotEmpty(task.getSymbol(), "symbol");
        isNotZero(task.getInitialBase(), "capitalBase");
        isNotEmpty(task.getTimeInterval(), "timeInterval");
        isNotEmpty(task.getStartTime(), "start");
        isNotEmpty(task.getEndTime(), "end");

        try {
            logger.debug("[ BACK TEST ] RUN {}", task);

            int resultCount = sqlSession.insert("backtest.insertHistory", task);
            if(resultCount != 1) {
                throw new OperationException("[FAIL] Insert Failed Test History. result count: " + resultCount);
            }
            logger.info("BackTest Task Id: {}", task.getId());

            int taskId = task.getId();
            dockerUtils.run(taskId, task.getRunEnv(), task.getRunCommand());

            TaskFuture<Map<String, Object>> future = backTestResult.get(taskId);
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

    public Map<String, Object> registerBacktestResult(Integer id, Map<String, Object> resultJson) {
        TaskFuture<Map<String, Object>> taskFuture = new TaskFuture();
        taskFuture.offer(resultJson);
        backTestResult.put(id, taskFuture);
        backTestLogger.debug("[{}] BackTest Result Saved.", id);
        return resultJson;
    }

    public Task runAgentTask(String userId, String accessToken, Integer agentId, boolean isLiveMode) throws ParameterException, OperationException {
//        TODO 에이전트 cctrader 변경
        throw new OperationException("개발예정");
//
//        Task task = getAgentTaskFromId(agentId);
//
//        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(task.getExchangeKeyId(), userId));
//        RunBackTestRequest.ExchangeAuth exchangeAuth = null;
//        String exchangeName = exchangeKey.getExchangeName();
//
//        List<KeyValuePair> environmentList = new ArrayList<>();
//        if(isLiveMode) {
//            environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(exchangeAuth.getKey()));
//            environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(exchangeAuth.getSecret()));
//        }
//        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));
//        environmentList.add(new KeyValuePair().withName("user_id").withValue(userId));
//        environmentList.add(new KeyValuePair().withName("access_token").withValue(accessToken));
//        environmentList.add(new KeyValuePair().withName("agent_id").withValue(String.valueOf(agentId)));
//
//        logger.debug("[ LIVE={} ] RUN {}", isLiveMode, task);
//        RunTaskResult result = null;
//        try {
//            result = ecsUtils.runTask(task, environmentList);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] ecs task run error");
//        }
//
//        String ecsTaskId = parseTaskId(result);
//        task.setEcsTaskId(ecsTaskId);
//        logger.info("starting ecs: {}", ecsTaskId);
//        Agent agent = new Agent();
//        agent.setId(task.getId());
//        agent.setEcsTaskId(ecsTaskId);
//        agent.setState(Agent.STATE_RUN);
//        agentService.updateAgent(agent);
//        return task;
    }


    public Task stopAgentTask(String userId, Integer agentId) throws ParameterException, OperationException {
//        TODO 에이전트 정지
        throw new OperationException("개발예정");
//        Agent agent = agentService.getAgent(agentId);
//        Task task = new Task();
//        task.setEcsTaskId(agent.getEcsTaskId());
//        StopTaskResult stopTaskResult = null;
//        try {
//            stopTaskResult = ecsUtils.stopTask(task.getEcsTaskId(), "User stop request : " + userId + ", " + agentId);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] ecs task run error");
//        }
//        com.amazonaws.services.ecs.model.Task stopedTask = stopTaskResult.getTask();
//        logger.debug("Stopped task : {}", stopedTask);
//        agent.setState(Agent.STATE_STOP);
//        agentService.updateAgent(agent);
//        return task;

    }


    private String parseTaskId(RunTaskResult result) {
        return result.getTasks().get(0).getTaskArn().split("/")[1];
    }

    private Task getAgentTaskFromId(Integer agentId) throws ParameterException, OperationException {

        //agent 테이블을 읽어서 Task에 채워준다.
        Agent agent = agentService.getAgent(agentId);
        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(agent.getExchangeKeyId(), agent.getUserId()));
        Task task = agent.cloneTask();
        task.setExchangeName(exchangeKey.getExchangeName());
        task.setExchangeKeyId(agent.getExchangeKeyId());
        return task;
    }

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

    public List<Task> getBackTestHistory(String strategyId) throws OperationException {
        try {
            return sqlSession.selectList("backtest.selectHistory", strategyId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Select Test History");
        }
    }

    public void deleteBackTestHistory(Strategy strategy) throws OperationException {
        try {
            sqlSession.delete("backtest.deleteTestHistory", strategy);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Select Test History");
        }
    }

    public Strategy getBackTestModel(Integer testId, String userId, Integer version) throws ParameterException, OperationException {
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

    public ConcurrentMap<Integer, TaskFuture> getBackTestResult() {
        return this.backTestResult;
    }
}