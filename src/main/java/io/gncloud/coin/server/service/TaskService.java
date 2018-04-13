package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.amazonaws.services.ecs.model.StopTaskResult;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.message.RunBackTestRequest;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.model.ExchangeKey;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.utils.AwsUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Resource(name = "awsUtils")
    private AwsUtils awsUtils;

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

    public Task runBackTestTask(String userId, Task task) throws ParameterException, OperationException {

        isNotEmpty(task.getStrategyId(), "strategyId");
        isNotEmpty(task.getExchangeName(), "exchangeName");
        isNotEmpty(task.getBaseCurrency(), "baseCurrency");
        isNotZero(task.getCapitalBase(), "capitalBase");
        isNotEmpty(task.getDataFrequency(), "dataFrequency");
        isNotEmpty(task.getStartTime(), "start");
        isNotEmpty(task.getEndTime(), "end");

        Strategy strategy = strategyService.getStrategy(task.getStrategyId());

        task.setStrategyVersion(strategy.getVersion());

        try {
            logger.debug("[ BACK TEST ] RUN {}", task);

            int resultCount = sqlSession.insert("backtest.insertHistory", task);
            if(resultCount != 1){
                throw new OperationException("[FAIL] Insert Failed Test History. result count: " + resultCount);
            }

            List<KeyValuePair> environmentList = new ArrayList<>();
            environmentList.add(new KeyValuePair().withName("user_token").withValue(userId));
            environmentList.add(new KeyValuePair().withName("test_id").withValue(task.getId().toString()));
            environmentList.add(new KeyValuePair().withName("user_id").withValue(task.getUserId()));

            RunTaskResult result = awsUtils.runTask(task, environmentList);

            String ecsTaskId = result.getTasks().get(0).getTaskArn().split("/")[1];
            logger.debug("ecs task id: {}", ecsTaskId);
            task.setEcsTaskId(ecsTaskId);
            return task;
        } catch (Throwable t){
            logger.error("", t);
            throw new OperationException("[FAIL] Running BackTest.");
        }
    }

    public Task runAgentTask(String userId, Integer agentId, boolean isLiveMode) throws ParameterException, OperationException {

        Task task = getAgentTaskFromId(agentId);

        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(task.getExchangeKeyId(), userId));
        RunBackTestRequest.ExchangeAuth exchangeAuth = null;
        String exchangeName = exchangeKey.getExchangeName();

        List<KeyValuePair> environmentList = new ArrayList<>();
        if(isLiveMode) {
            environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(exchangeAuth.getKey()));
            environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(exchangeAuth.getSecret()));
        }
        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));
        environmentList.add(new KeyValuePair().withName("user_token").withValue(userId));

        logger.debug("[ LIVE={} ] RUN {}", isLiveMode, task);
        RunTaskResult result = awsUtils.runTask(task, environmentList);

        String ecsTaskId = parseTaskId(result);
        task.setEcsTaskId(ecsTaskId);

        Agent agent = new Agent();
        //TODO agent 값 셋팅
        agent.setEcsTaskId(ecsTaskId);
        agentService.updateAgent(agent);
        return task;
    }


    public Task stopAgentTask(String userId, Integer agentId) throws ParameterException, OperationException {
        Agent agent = agentService.getAgent(agentId);
        Task task = new Task();
        task.setEcsTaskId(agent.getEcsTaskId());
        StopTaskResult stopTaskResult = awsUtils.stopTask(task.getEcsTaskId(), "User stop request : " + userId + ", " + agentId);
        com.amazonaws.services.ecs.model.Task stopedTask = stopTaskResult.getTask();

        logger.debug("Stopped task : {}", stopedTask);
        return task;
    }


    private String parseTaskId(RunTaskResult result) {
        return result.getTasks().get(0).getTaskArn().split("/")[1];
    }

    private Task getAgentTaskFromId(Integer agentId) throws ParameterException, OperationException {

        //agent 테이블을 읽어서 Task에 채워준다.
        Agent agent = agentService.getAgent(agentId);
        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(agent.getExchangeKeyId(), agent.getUserId()));
        Task task = agent.getTask();
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


}