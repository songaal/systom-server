package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskResult;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.RequestTask;
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

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Service
public class TasksService {

    private static Logger logger = LoggerFactory.getLogger(TasksService.class);

    @Resource(name = "awsUtils")
    private AwsUtils awsUtils;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private SqlSession sqlSession;

    public Task backTestMode(String token, RequestTask requestTask) throws ParameterException, AuthenticationException, OperationException {
        Task task = requestTask.getTask();

        isNull(task.getStrategyId(), "StrategyId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getBaseCurrency(), "Currency");
        isNull(task.getCapitalBase(), "StartMoney");
        isNull(task.getDataFrequency(), "dataFrequency");
        isNull(task.getStartTime(), "start");
        isNull(task.getEndTime(), "end");

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
            Task resultTask = sqlSession.selectOne("test.lastBackTest", task);

            List<KeyValuePair> environmentList = new ArrayList<>();
            environmentList.add(new KeyValuePair().withName("user_token").withValue(token));
            environmentList.add(new KeyValuePair().withName("test_id").withValue(resultTask.getId()));
            environmentList.add(new KeyValuePair().withName("user_id").withValue(task.getUserId()));

            RunTaskResult result = awsUtils.runTask(token, task, environmentList);

            String ecsTask = result.getTasks().get(0).getTaskArn().split("/")[1];
            logger.debug("ecs task id: {}", ecsTask);
            resultTask.setEcsTask(ecsTask);
            return resultTask;
        } catch (Throwable t){
            logger.error("", t);
            throw new OperationException("[FAIL] Insert Test History");
        }
    }

    public Task liveMode(String token, RequestTask requestTask) throws ParameterException, AuthenticationException, OperationException {
        Task task = requestTask.getTask();

        isNull(task.getStrategyId(), "StrategyId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getBaseCurrency(), "Currency");
        isNull(task.getCapitalBase(), "StartMoney");

        Strategy strategy = strategyService.getStrategy(token, requestTask.getTask().getStrategyId());
        //TODO 전략 사용 가능한 지 확인 필요

        String exchangeName = requestTask.getExchangeAuth().getExchange();
        String key = requestTask.getExchangeAuth().getKey();
        String secret = requestTask.getExchangeAuth().getSecret();

        List<KeyValuePair> environmentList = new ArrayList<>();
        environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(key));
        environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(secret));
        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));
        environmentList.add(new KeyValuePair().withName("user_token").withValue(token));

        logger.debug("[ LIVE ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(token, task, environmentList);

        task.setEcsTask(result.getTasks().get(0).getTaskArn().split("/")[1]);
        return task;
    }

    private void isNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNull(float field, String label) throws ParameterException {
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