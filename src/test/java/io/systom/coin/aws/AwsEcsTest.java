package io.systom.coin.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 3. 20.
 * 
 */
public class AwsEcsTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AwsEcsTest.class);
    private String profileName = "gncloud";
    private String clusterId = "systom";
    String family = "test-netcat";
    String signalName = "signal";
    String executorName = "executor";

    private AmazonECS client;

    @Before
    public void setup(){
         client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
    }

    @Test
    public void runTask(){
        String reversion = "4";
        String taskDefinition = family + ":" + reversion;

        RunTaskRequest runTaskRequest = new RunTaskRequest();

        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride signalContainerOverride = new ContainerOverride();
        ContainerOverride executorContainerOverride = new ContainerOverride();

        signalContainerOverride.withName(signalName)
                .withCommand("ping","executor");

        executorContainerOverride.withName(executorName)
                .withCommand("nc","-lk","5000");

        taskOverride.withContainerOverrides(signalContainerOverride, executorContainerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = client.runTask(runTaskRequest);

        logger.info("result {}, {}", result, result.getTasks().get(0).getTaskArn());

        result.getTasks().get(0).getTaskArn();

    }

    @Test
    public void listTest() {
        ListTasksRequest listTasksRequest = new ListTasksRequest();
        listTasksRequest.withCluster(clusterId);

        client.listTasks(new ListTasksRequest().withCluster(clusterId));

//        arn:aws:ecs:ap-northeast-2:868448630378:task/afce2307-94b4-4756-af36-4ca1b89b218f
//        client.describeTasks(new DescribeTasksRequest().withTasks("arn:aws:ecs:ap-northeast-2:868448630378:task/afce2307-94b4-4756-af36-4ca1b89b218f").withCluster(clusterId))
        ListTasksResult tasksResult= client.listTasks();
        tasksResult.getTaskArns().forEach(s -> {
            logger.info("arns: {}", s);
        });
    }














//    @Test
//    public void createTaskTest(){
////        =========================== parameter start
//        Integer goodsId = 6;
////        =========================== parameter end
//
//        LogConfiguration logConfiguration = new LogConfiguration();
//        logConfiguration.withLogDriver(LogDriver.Awslogs)
//                        .withOptions(getLogOption(goodsId));
//
//        RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
//                .withFamily(family)
//                .withTaskRoleArn(taskExcutionRole)
//                .withContainerDefinitions(
//                        new ContainerDefinition().withName(name)
//                                                 .withImage(image)
//                                                 .withCpu(cpu)
//                                                 .withEssential(true)
//                                                 .withMemoryReservation(softMemory)
//                                                 .withLogConfiguration(logConfiguration)
//                                                 .withMountPoints(setupMount())
//                );
//
//        RegisterTaskDefinitionResult response = client.registerTaskDefinition(request);
//        logger.info("taskdifinotion:reversion => {}:{}", response.getTaskDefinition().getFamily(), response.getTaskDefinition().getRevision());
//    }




//    protected Map<String, String> getLogOption(Integer goodsId) {
//        Map<String, String> logOption = new HashMap<>();
//        logOption.put("awslogs-region", logRegion);
//        logOption.put("awslogs-group", logGroup);
//        logOption.put("awslogs-stream-prefix", "" + goodsId);
//        return logOption;
//    }








//    @Test
//    public void ingest(){
//        //bitfinex, polonex, bittrex, binance
//        String exchangeName = "polonex";
//        //daliy, minute
//        String dataFrequency = "minute";
//        // symbol
//        String symbol = "btc_usdt";
//
//        String taskDefinition = "ingest-exchange";
//        String reversion = "5";
//
//        RunTaskRequest runTaskRequest = new RunTaskRequest();
//
//        TaskOverride taskOverride = new TaskOverride();
//        ContainerOverride containerOverride = new ContainerOverride();
//
//        containerOverride.withName("exchange-bitfinex")
//                .withCommand("ingest-exchange"
//                        , "-x"
//                        , exchangeName
//                        , "-f"
//                        , dataFrequency
//                        , "-i"
//                        , symbol
//                        , "--show-progress");
//        taskOverride.withContainerOverrides(containerOverride);
//
//        runTaskRequest.withTaskDefinition(taskDefinition + ":" + reversion)
//                .withOverrides(taskOverride)
//                .withCluster(clusterId);
//        RunTaskResult result = client.runTask(runTaskRequest);
//
//        logger.info("result {}", result);
//    }






    private List<Volume> setupVolume(String user, String algorithmFile){
        List<Volume> volumeList = new ArrayList<Volume>();
//        volumeList.add(newVolume(runAlgoName, String.format("/home/ec2-user/%s/%s", user, algorithmFile)));
//        volumeList.add(newVolume(dataVolumeName, "/home/ec2-user/coincloud/data"));
//        volumeList.add(newVolume(extVolumeName, String.format("/home/%s/ext", user)));
        return volumeList;
    }

    private List<MountPoint> setupMount(){
        List<MountPoint> mountPointList = new ArrayList<MountPoint>();
//        mountPointList.add(newMount(runAlgoName, runAlgoPath, false));
//        mountPointList.add(newMount(dataVolumeName, dataPath, true));
//        mountPointList.add(newMount(extVolumeName, extPath, false));
        return mountPointList;
    }

    private MountPoint newMount(String volumeName, String mountPath, boolean readyOnly){
        return new MountPoint().withSourceVolume(volumeName)
                               .withContainerPath(mountPath)
                               .withReadOnly(readyOnly);
    }

    private Volume newVolume(String volumeName, String path){
        HostVolumeProperties volumeProperties = new HostVolumeProperties();
        volumeProperties.setSourcePath(path);
        return new Volume().withHost(volumeProperties)
                           .withName(volumeName);
    }

    private KeyValuePair newEnv(String key, String value){
        return new KeyValuePair().withName(key)
                                 .withValue(value);
    }
}