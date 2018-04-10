package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskResult;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.message.RunBackTestRequest;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.model.User;
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
    private SqlSession sqlSession;

    public Task runBackTestTask(String token, Task task) throws ParameterException, AuthenticationException, OperationException {

        isNotEmpty(task.getStrategyId(), "strategyId");
        isNotEmpty(task.getExchangeName(), "exchangeName");
        isNotEmpty(task.getBaseCurrency(), "baseCurrency");
        isNotZero(task.getCapitalBase(), "capitalBase");
        isNotEmpty(task.getDataFrequency(), "dataFrequency");
        isNotEmpty(task.getStartTime(), "start");
        isNotEmpty(task.getEndTime(), "end");

        Strategy strategy = strategyService.getStrategy(token, task.getStrategyId());

        User user = identityService.findTokenByUser(token);
        task.setUserId(user.getUserId());
        task.setStrategyVersion(strategy.getVersion());

        try {
            logger.debug("[ BACK TEST ] RUN {}", task);

            int resultCount = sqlSession.insert("test.insertTestHistory", task);
            if(resultCount != 1){
                throw new OperationException("[FAIL] Insert Failed Test History. result count: " + resultCount);
            }
            Task resultTask = sqlSession.selectOne("testing.selectLatestTestHistory", task);

            List<KeyValuePair> environmentList = new ArrayList<>();
            environmentList.add(new KeyValuePair().withName("user_token").withValue(token));
            environmentList.add(new KeyValuePair().withName("test_id").withValue(resultTask.getId()));
            environmentList.add(new KeyValuePair().withName("user_id").withValue(task.getUserId()));

            RunTaskResult result = awsUtils.runTask(token, task, environmentList);

            String ecsTaskId = result.getTasks().get(0).getTaskArn().split("/")[1];
            logger.debug("ecs task id: {}", ecsTaskId);
            resultTask.setEcsTaskId(ecsTaskId);
            return resultTask;
        } catch (Throwable t){
            logger.error("", t);
            throw new OperationException("[FAIL] Running BackTest.");
        }
    }

    public Task runLiveAgentTask(String token, String agentId, String exchangeName, String userPin) throws ParameterException, AuthenticationException, OperationException {

        Task task = getAgentTaskFromId(agentId);

        RunBackTestRequest.ExchangeAuth exchangeAuth = null;
        if(!task.isSimulationOrder()) {
            exchangeAuth = strategyService.getExchangeAuth(token, exchangeName, userPin);
        }

        Strategy strategy = strategyService.getStrategy(token, task.getStrategyId());

        List<KeyValuePair> environmentList = new ArrayList<>();
        environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(exchangeAuth.getKey()));
        environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(exchangeAuth.getSecret()));
        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeAuth.getExchange()));
        environmentList.add(new KeyValuePair().withName("user_token").withValue(token));

        logger.debug("[ LIVE ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(token, task, environmentList);

        String ecsTaskId = parseTaskId(result);
        task.setEcsTaskId(ecsTaskId);
        return task;
    }

    private String parseTaskId(RunTaskResult result) {
        return result.getTasks().get(0).getTaskArn().split("/")[1];
    }

    private Task getAgentTaskFromId(String agentId) {
        return null;
    }

    private void isNotEmpty(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotZero(float field, String label) throws ParameterException {
        if(field == 0.0f){
            throw new ParameterException(label);
        }
    }

    public List<Task> getBackTestHistory(String token, String strategysId) throws OperationException {
        try {
            return sqlSession.selectList("test.getBackTestHistory", strategysId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Select Test History");
        }

    }

}