package io.gncloud.coin.server.service;

import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.RunTaskResult;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.utils.AwsUtils;
import org.slf4j.LoggerFactory;
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

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TasksService.class);

    @Resource(name = "awsUtils")
    private AwsUtils awsUtils;

    public Task backTestMode(Task task) throws ParameterException {

        isNull(task.getAlgoId(), "algoId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getBaseCurrency(), "baseCurrency");
        isNull(task.getCapitalBase(), "capitalBase");
        isNull(task.getDataFrequency(), "dataFrequency");
        isNull(task.getStart(), "start");
        isNull(task.getEnd(), "end");

        logger.debug("[ BACK TEST ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(task);
        task.setTaskId(result.getTasks().get(0).getTaskArn().split("/")[1]);
        return task;
    }

    public Task liveMode(Task task, List<RequestTask.ExchangeAuth> exchangeAuth) throws ParameterException {

        isNull(task.getAlgoId(), "algoId");
        isNull(task.getExchangeName(), "exchangeName");
        isNull(task.getBaseCurrency(), "baseCurrency");
        isNull(task.getCapitalBase(), "capitalBase");

        List<KeyValuePair> environmentList = new ArrayList<>();
        String exchangeList = new String();
        int exchangeAuthSize = exchangeAuth.size();
        if(exchangeAuthSize > 0){
            for (int i = 0; i < exchangeAuthSize; i++) {
                String exchangeName = exchangeAuth.get(i).getExchange();
                String key = exchangeAuth.get(i).getKey();
                String secret = exchangeAuth.get(i).getSecret();
                exchangeList += exchangeName + ",";

                environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(key));
                environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(secret));
            }
            environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeList));
        }

        logger.debug("[ LIVE ] RUN {}", task);
        RunTaskResult result = awsUtils.runTask(task, environmentList);

        task.setTaskId(result.getTasks().get(0).getTaskArn().split("/")[1]);
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
}