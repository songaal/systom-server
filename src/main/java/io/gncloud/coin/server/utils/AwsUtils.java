package io.gncloud.coin.server.utils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import io.gncloud.coin.server.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@Component("awsUtils")
public class AwsUtils {

    @Value("${aws.ProfileName}")
    private String awsProfileName;
    @Value("${aws.ecs.difinition.name}")
    private String taskDifiniName;
    @Value("${aws.ecs.difinition.version}")
    private String taskDifiniVersion;
    @Value("${aws.ecs.difinition.container}")
    private String container;
    @Value("${aws.ecs.clusterId}")
    private String clusterId;


    private AmazonECS client;

    @PostConstruct
    public void setup(){
        client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(awsProfileName))
                .build();
    }
    public RunTaskResult runTask(String token, Task task){
        return runTask(token, task, null);
    }
    public RunTaskResult runTask(String token, Task task, List<KeyValuePair> environmentList){
        RunTaskRequest runTaskRequest = new RunTaskRequest();
        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride containerOverride = new ContainerOverride();
        containerOverride.withName(container)
                         .withCommand(task.getRunCommand());
        if(environmentList != null && environmentList.size() > 0){
            containerOverride.withEnvironment(environmentList);
        }
        taskOverride.withContainerOverrides(containerOverride);

        runTaskRequest.withTaskDefinition(taskDifiniName + ":" + taskDifiniVersion)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        return client.runTask(runTaskRequest);
    }


}