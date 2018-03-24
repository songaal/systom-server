package io.gncloud.coin.server.service;

import io.gncloud.coin.server.config.Env;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.utils.AwsUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Service
public class TasksService {

    @Resource(name = "awsUtils")
    private AwsUtils awsUtils;

    private final String ALGO_PATH = "/algos";


    public File fileWrite(String taskId, String source) throws IOException {
        File sourceDirectory = new File(Env.getCoinHome() + ALGO_PATH);
        if(!sourceDirectory.isDirectory()){
            sourceDirectory.mkdirs();
        }
        File AlgoSource = new File(sourceDirectory.getPath() + "/" + taskId + ".py");
        OutputStreamWriter ouput = new OutputStreamWriter(new FileOutputStream(AlgoSource));
        ouput.write(source);
        ouput.flush();
        ouput.close();
        return AlgoSource;
    }

    public void backTestMode(String taskId, String exchangeName, String baseCurrency, float capitalBase,
                             String dataFrequency, String start, String end) throws ParameterException {

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

    public void liveMode(String algoId, String exchangeName, String baseCurrency, float capitalBase, boolean simulationOrder) throws ParameterException {
        if(exchangeName == null || "".equals(exchangeName)){
            throw new ParameterException("exchangeName");
        }
        if(baseCurrency == null || "".equals(baseCurrency)){
            throw new ParameterException("baseCurrency");
        }
        if(capitalBase == 0.0f){
            throw new ParameterException("capitalBase");
        }


        RequestTask task = new RequestTask();
        task.setTaskId(algoId);
        task.setExchangeName(exchangeName);
        task.setBaseCurrency(baseCurrency);
        task.setCapitalBase(capitalBase);
        task.setSimulationOrder(simulationOrder);
        task.setLive(true);

        awsUtils.runTask(task);
    }

}