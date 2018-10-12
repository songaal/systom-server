package io.systom.coin.utils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import io.systom.coin.config.Env;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.TraderTask;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Component
public class EcsUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(EcsUtils.class);
    @Value("${aws.profileName}")
    private String awsProfileName;
    @Value("${aws.region}")
    private String awsReason;
    @Value("${aws.ecs.clusterId}")
    private String clusterId;
    @Value("${aws.ecs.definition.container.signal}")
    private String signalName;
    @Value("${aws.ecs.definition.container.executor}")
    private String executorName;
    @Value("${aws.ecs.definition.name}")
    private String taskDefinitionName;
    @Value("${aws.ecs.definition.version}")
    private String taskDefinitionVersion;
    @Value("${backtest.apiServerUrl}")
    private String apiServerUrl;

    @Value("${invest.start.hour}")
    private String startHour;
    @Value("${invest.start.minute}")
    private String startMinute;
    @Value("${invest.start.second}")
    private String startSecond;

    private AmazonECS client;

    @PostConstruct
    public void init(){
        client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(awsProfileName))
                .build();
    }

    public Task syncRun(TraderTask traderTask){
        logger.info("ECS TraderTask Start");
        String taskDefinition = taskDefinitionName + ":" + taskDefinitionVersion;
//        날짜만 전달.
//        String startTime = String.format("%s:%s:%s", startHour, startMinute, startSecond);
//        List<String> signalCmd = traderTask.getLiveSignalCmd(startTime);
        List<String> signalCmd = traderTask.getLiveSignalCmd();
        signalCmd.add("api_server_url=" + apiServerUrl);
        List<String> executorCmd = traderTask.getLiveExecutorCmd(Env.isLiveExecution());

        List<KeyValuePair> signalEnv = traderTask.getLiveSignalEnv();
        List<KeyValuePair> executorEnv = traderTask.getLiveExecutorEnv();

        logger.info("clusterId: {}", clusterId);
        logger.info("taskDefinition: {}", taskDefinition);
        logger.info("signal command: {}", signalCmd);
        logger.info("executor command: {}", executorCmd);

        RunTaskRequest runTaskRequest = new RunTaskRequest();
        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride signalContainerOverride = new ContainerOverride();
        ContainerOverride executorContainerOverride = new ContainerOverride();
        // signal run..
        signalContainerOverride.withName(signalName)
                .withEnvironment(signalEnv)
                .withCommand(signalCmd);
        // executor run..
        executorContainerOverride.withName(executorName)
                .withEnvironment(executorEnv)
                .withCommand(executorCmd);

        taskOverride.withContainerOverrides(signalContainerOverride, executorContainerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = null;
        String taskArn = null;
        try {
            long st = System.nanoTime();
            result = client.runTask(runTaskRequest);
            if (result.getFailures().size() > 0) {
                result.getFailures().forEach(failure -> {
                    logger.error("[ECS ERROR] ** arn:{}, reason: {}", failure.getArn(), failure.getReason());
                });
                throw new OperationException(result.getFailures().get(0).toString());
            }
            taskArn = result.getTasks().get(0).getTaskArn();
            logger.debug("ECS TASK ID: {},  ARN: {}", traderTask.getId(), taskArn);
            while (true) {
                Task task = getDescribeTasks(taskArn);
                String status = task.getLastStatus();
                if ("RUNNING".equalsIgnoreCase(status)) {
                    logger.info("ECS Task Running. {}", task);
                    break;
                } else if("STOPPED".equalsIgnoreCase(status)) {
                    logger.info("ECS Task Running Error.", task);
                    throw new Exception("ECS Task Running Error");
                } else {
                    logger.debug("[{}] status:{} taskArn: {}", traderTask.getId(), status, taskArn);
                }
                long ct = System.nanoTime() - st;
                if (ct > 300000000000l) {
                    logger.error("[ECS RUN Exception] time: {}, timeout: {}", ct, 300000000000l);
                    throw new Exception("[ECS RUN Exception] Timeout");
                } else {
                    // 5초 쉬면서 RUNNING 확인
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e) {
            logger.error("[fail] goodsId: {}", traderTask.getGoodsId(), e);
            try {
                stopTask(taskArn);
            } catch(Throwable t) {
                logger.error(t.getMessage());
            }
            throw new OperationException(e.getMessage());
        }
        return result.getTasks().get(0);
    }

    public Task getDescribeTasks(String taskEcsIds) {
        if (client.describeTasks(new DescribeTasksRequest().withTasks(taskEcsIds).withCluster(clusterId)).getTasks().size() == 0) {
            return new Task();
        } else {
            return client.describeTasks(new DescribeTasksRequest().withTasks(taskEcsIds).withCluster(clusterId)).getTasks().get(0);
        }
    }

    public List<Task> getDescribeTasks(List<String> taskEcsIds) {
        return client.describeTasks(new DescribeTasksRequest().withTasks(taskEcsIds).withCluster(clusterId)).getTasks();
    }

    public List<String> getRunningTaskList() {
        return client.listTasks(new ListTasksRequest().withCluster(clusterId)).getTaskArns();
    }

    public Task stopTask(String taskEcsId){
        if (taskEcsId == null) {
            return null;
        }
        logger.debug("[{}] task stop..", taskEcsId);
        StopTaskRequest stopTaskRequest = new StopTaskRequest();
        stopTaskRequest.withReason(awsReason)
                .withTask(taskEcsId)
                .withCluster(clusterId);
        StopTaskResult stopTaskResult = client.stopTask(stopTaskRequest);
//        stopTaskResult.getTask().getLastStatus();
        return stopTaskResult.getTask();
    }
}