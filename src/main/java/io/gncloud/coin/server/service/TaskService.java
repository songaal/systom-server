package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.utils.AwsUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Service
public class TaskService {

    @Resource(name = "awkUtils")
    private AwsUtils awsUtils;

    public void backTestMode(String userId, String exchangeName, String baseCurrency, float capitalBase,
                             String dataFrequency, Date start, Date end) throws ParameterException {

        if(exchangeName == null || "".equals(exchangeName)){
            throw new ParameterException("exchangeName");
        }
        if(baseCurrency == null || "".equals(baseCurrency)){
            throw new ParameterException("baseCurrency");
        }
        if(capitalBase == 0.0f){
            throw new ParameterException("capitalBase");
        }
        if(dataFrequency == null || "".equals(dataFrequency)){
            throw new ParameterException("dataFrequency");
        }
        if(start == null){
            throw new ParameterException("startTime");
        }
        if(end == null){
            throw new ParameterException("end");
        }

        String taskId = UUID.randomUUID().toString();
        RequestTask task = new RequestTask();
        task.setTaskId(taskId);
        task.setExchangeName(exchangeName);
        task.setBaseCurrency(baseCurrency);
        task.setCapitalBase(capitalBase);
        task.setDataFrequency(dataFrequency);
        task.setStart(start);
        task.setEnd(end);

        awsUtils.runTask(task);
    }

    public void liveMode(String userId, String exchangeName, String baseCurrency, float capitalBase, boolean simulationOrder) throws ParameterException {
        if(exchangeName == null || "".equals(exchangeName)){
            throw new ParameterException("exchangeName");
        }
        if(baseCurrency == null || "".equals(baseCurrency)){
            throw new ParameterException("baseCurrency");
        }
        if(capitalBase == 0.0f){
            throw new ParameterException("capitalBase");
        }

        String taskId = UUID.randomUUID().toString();
        RequestTask task = new RequestTask();
        task.setTaskId(taskId);
        task.setExchangeName(exchangeName);
        task.setBaseCurrency(baseCurrency);
        task.setCapitalBase(capitalBase);
        task.setSimulationOrder(simulationOrder);

        awsUtils.runTask(task);
    }

}