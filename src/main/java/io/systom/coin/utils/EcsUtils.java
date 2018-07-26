package io.systom.coin.utils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
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

        logger.info("clusterId: {}", clusterId);
        logger.info("taskDefinition: {}", taskDefinition);
        logger.info("signal command: {}", traderTask.getLiveSignalCmd());
        logger.info("executor command: {}", traderTask.getLiveExecutorCmd());

        List<String> cmd = traderTask.getLiveSignalCmd();
        cmd.add("api_server_url=" + apiServerUrl);

        RunTaskRequest runTaskRequest = new RunTaskRequest();
        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride signalContainerOverride = new ContainerOverride();
        ContainerOverride executorContainerOverride = new ContainerOverride();
        signalContainerOverride.withName(signalName)
                .withCommand(cmd);

        executorContainerOverride.withName(executorName)
                .withCommand(traderTask.getLiveExecutorCmd());

        taskOverride.withContainerOverrides(signalContainerOverride, executorContainerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = null;
        String taskArn = null;
        try {
            long st = System.nanoTime();
            result = client.runTask(runTaskRequest);
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
            stopTask(taskArn);
            logger.error("[fail] goodsId: {}", traderTask.getGoodsId(), e);
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
        StopTaskRequest stopTaskRequest = new StopTaskRequest();
        stopTaskRequest.withReason(awsReason)
                .withTask(taskEcsId)
                .withCluster(clusterId);
        StopTaskResult stopTaskResult = client.stopTask(stopTaskRequest);
//        stopTaskResult.getTask().getLastStatus();
        return stopTaskResult.getTask();
    }
}