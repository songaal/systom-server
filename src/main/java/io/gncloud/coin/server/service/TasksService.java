package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.google.gson.Gson;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.utils.AwsUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Service
public class TasksService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TasksService.class);

    @Resource(name = "awsUtils")
    private AwsUtils awsUtils;

    @Autowired
    private AuthService authService;

    @Autowired
    private StrategyService strategyService;

    public Task backTestMode(String token, RequestTask requestTask) throws ParameterException, AuthenticationException, OperationException {
        Task task = requestTask.getTask();

        isNull(task.getStrategyId(), "StrategyId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getCurrency(), "Currency");
        isNull(task.getStartMoney(), "StartMoney");
        isNull(task.getDataFrequency(), "dataFrequency");
        isNull(task.getStart(), "start");
        isNull(task.getEnd(), "end");

        Strategy strategy = strategyService.getStrategy(token, requestTask.getTask().getStrategyId());
        isOptionValid(strategy.getOptions(), task.getOptions());

        logger.debug("[ BACK TEST ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(task);
        task.setEcsTask(result.getTasks().get(0).getTaskArn().split("/")[1]);

        return task;
    }

    public Task liveMode(String token, RequestTask requestTask) throws ParameterException, AuthenticationException, OperationException {
        Task task = requestTask.getTask();

        isNull(task.getStrategyId(), "StrategyId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getCurrency(), "Currency");
        isNull(task.getStartMoney(), "StartMoney");

        Strategy strategy = strategyService.getStrategy(token, requestTask.getTask().getStrategyId());
        isOptionValid(strategy.getOptions(), task.getOptions());

        String exchangeName = requestTask.getExchangeAuth().getExchange();
        String key = requestTask.getExchangeAuth().getKey();
        String secret = requestTask.getExchangeAuth().getSecret();

        List<KeyValuePair> environmentList = new ArrayList<>();
        environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(key));
        environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(secret));
        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));

        logger.debug("[ LIVE ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(task, environmentList);

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

    public void isOptionValid(String srcOption, String targetOptions) throws ParameterException {
        Map<String, String> options = new Gson().fromJson(srcOption, Map.class);
        Map<String, String> runOptions = new Gson().fromJson(targetOptions, Map.class);

        Iterator<String> iterator = options.keySet().iterator();
        while(iterator.hasNext()){
            String envKey = iterator.next();
            String optionValue = runOptions.get(envKey);
            if(optionValue == null || "".equals(optionValue)){
                throw new ParameterException(envKey);
            }
        }
    }
}