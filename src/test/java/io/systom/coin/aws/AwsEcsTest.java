package io.systom.coin.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 20.
 * 
 */
public class AwsEcsTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AwsEcsTest.class);
    private String profileName = "gncloud";
    private String taskExcutionRole = "ecsTaskExecutionRole";
    private String clusterId = "coincloud";
    private String image = "868448630378.dkr.ecr.ap-northeast-2.amazonaws.com/systom-signal";
    private int cpu = 1;
    private int softMemory = 500;
    private int hardMemory = 0;
    private String logRegion = "ap-northeast-2";
    private String logGroup = "ecs-container";
    private AmazonECS client;

    @Before
    public void setup(){
         client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
    }

    @Test
    public void createTaskTest(){
//        =========================== parameter start
        Integer goodsId = 6;
        String user = "joonwoo";
        String family = "goods-6-binance-eth";
        String name = "test6";
//        =========================== parameter end

        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.withLogDriver(LogDriver.Awslogs)
                        .withOptions(getLogOption(goodsId));

        RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
                .withFamily(family)
                .withTaskRoleArn(taskExcutionRole)
                .withContainerDefinitions(
                        new ContainerDefinition().withName(name)
                                                 .withImage(image)
                                                 .withCpu(cpu)
                                                 .withEssential(true)
                                                 .withMemoryReservation(softMemory)
                                                 .withLogConfiguration(logConfiguration)
                                                 .withMountPoints(setupMount())
                );

        RegisterTaskDefinitionResult response = client.registerTaskDefinition(request);
        logger.info("taskdifinotion:reversion => {}:{}", response.getTaskDefinition().getFamily(), response.getTaskDefinition().getRevision());
    }

    @Test
    public void runTask(){
        String taskDefinition = "tradebot";
        String reversion = "4";

        RunTaskRequest runTaskRequest = new RunTaskRequest();

        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride containerOverride = new ContainerOverride();
        List<KeyValuePair> envList = new ArrayList<>();

        containerOverride.withEnvironment(envList)
                         .withCommand("/bin/bash", "sleep 5");
        taskOverride.withContainerOverrides(containerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition + ":" + reversion)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = client.runTask(runTaskRequest);

        logger.info("result {}", result);
    }


    protected Map<String, String> getLogOption(Integer goodsId) {
        Map<String, String> logOption = new HashMap<>();
        logOption.put("awslogs-region", logRegion);
        logOption.put("awslogs-group", logGroup);
        logOption.put("awslogs-stream-prefix", "goodsId=" + goodsId);
        return logOption;
    }











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