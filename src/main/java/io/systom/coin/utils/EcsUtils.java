package io.systom.coin.utils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
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

    @Value("${aws.profileName}")
    private String awsProfileName;
    @Value("${aws.ecs.definition.name}")
    private String taskDefinitionName;
    @Value("${aws.ecs.definition.version}")
    private String taskDefinitionVersion;
    @Value("${aws.ecs.definition.container}")
    private String container;
    @Value("${aws.ecs.clusterId}")
    private String clusterId;

    private AmazonECS client;

    @PostConstruct
    public void init(){
        client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(awsProfileName))
                .build();
    }
    public RunTaskResult runTask(Task task){
        return runTask(task, null);
    }
    public RunTaskResult runTask(Task task, List<KeyValuePair> environmentList){
        RunTaskRequest runTaskRequest = new RunTaskRequest();
        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride containerOverride = new ContainerOverride();
//        containerOverride.withName(container)
//                         .withCommand(task.getRunCommand());
        if(environmentList != null && environmentList.size() > 0){
            containerOverride.withEnvironment(environmentList);
        }
        taskOverride.withContainerOverrides(containerOverride);

        runTaskRequest.withTaskDefinition(taskDefinitionName + ":" + taskDefinitionVersion)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        return client.runTask(runTaskRequest);
    }



    public StopTaskResult stopTask(String taskId, String reason){
        StopTaskRequest stopTaskRequest = new StopTaskRequest();
        stopTaskRequest.withReason(reason)
                .withTask(taskId)
                .withCluster(clusterId);
        return client.stopTask(stopTaskRequest);
    }
}