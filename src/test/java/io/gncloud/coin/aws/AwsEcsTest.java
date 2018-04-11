package io.gncloud.coin.aws;

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

    private String runAlgoName = "runAlgo";
    private String dataVolumeName = "data";
    private String extVolumeName = "ext";

    private String runAlgoPath = "/algo.py";
    private String dataPath = "/coincloud/data";
    private String extPath = "/coincloud/ext";

    private String taskExcutionRole = "ecsTaskExecutionRole";
    private String clusterId = "coincloud";
    private String image = "868448630378.dkr.ecr.ap-northeast-2.amazonaws.com/coincloud:latest";
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
        String user = "joonwoo";
        String algorithmFile = "my_simple.py";
        String coin = "btc";
        String mode = "backtest";
        String start = "2017-1-1";
        String end = "2017-02-1";
        long capitalBase = 1000L;
        String exchange = "bitfinex";
//        =========================== parameter end

        Map<String, String> logOption = new HashMap<String, String>();
        logOption.put("awslogs-region", logRegion);
        logOption.put("awslogs-group", logGroup);
        logOption.put("awslogs-stream-prefix", user);
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.withLogDriver(LogDriver.Awslogs)
                        .withOptions(logOption);

        RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
                .withFamily(user + "-" + mode + "-" + coin)
                .withTaskRoleArn(taskExcutionRole)
                .withVolumes(setupVolume(user, algorithmFile))
                .withContainerDefinitions(
                        new ContainerDefinition().withName(mode)
                                                 .withImage(image)
                                                 .withCpu(cpu)
                                                 .withEssential(true)
                                                 .withMemoryReservation(softMemory)
                                                 .withLogConfiguration(logConfiguration)
                                                 .withMountPoints(setupMount())
                                                 .withCommand("run", "-f", "algo.py"
                                                         , "-x", exchange
                                                         , "-c", coin
                                                         , "--capital-base", String.valueOf(capitalBase)
                                                         , "--start", start
                                                         , "--end", end)
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
        envList.add(newEnv("secretKey", "empty1"));
        envList.add(newEnv("accessKey", "empty2"));
        envList.add(newEnv("userId", "testuser"));
        envList.add(newEnv("testKey", "testVal"));

        containerOverride.withName("run")
                .withEnvironment(envList)
                .withCommand("python3"
                            ,"run.py"
                            ,"faa381fb-fa35-40c2-99e3-f863f1f3c913"
                            ,"bitfinex"
                            ,"usd"
                            ,"10000"
                            ,"false"
                            ,"2015-03-01"
                            ,"2017-10-31"
                            ,"minute");
        taskOverride.withContainerOverrides(containerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition + ":" + reversion)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = client.runTask(runTaskRequest);

        logger.info("result {}", result);
    }



    @Test
    public void ingest(){
        //bitfinex, polonex, bittrex, binance
        String exchangeName = "polonex";
        //daliy, minute
        String dataFrequency = "minute";
        // symbol
        String symbol = "btc_usdt";

        String taskDefinition = "ingest-exchange";
        String reversion = "5";

        RunTaskRequest runTaskRequest = new RunTaskRequest();

        TaskOverride taskOverride = new TaskOverride();
        ContainerOverride containerOverride = new ContainerOverride();

        containerOverride.withName("exchange-bitfinex")
                .withCommand("ingest-exchange"
                        , "-x"
                        , exchangeName
                        , "-f"
                        , dataFrequency
                        , "-i"
                        , symbol
                        , "--show-progress");
        taskOverride.withContainerOverrides(containerOverride);

        runTaskRequest.withTaskDefinition(taskDefinition + ":" + reversion)
                .withOverrides(taskOverride)
                .withCluster(clusterId);
        RunTaskResult result = client.runTask(runTaskRequest);

        logger.info("result {}", result);
    }






    private List<Volume> setupVolume(String user, String algorithmFile){
        List<Volume> volumeList = new ArrayList<Volume>();
        volumeList.add(newVolume(runAlgoName, String.format("/home/ec2-user/%s/%s", user, algorithmFile)));
        volumeList.add(newVolume(dataVolumeName, "/home/ec2-user/coincloud/data"));
        volumeList.add(newVolume(extVolumeName, String.format("/home/%s/ext", user)));
        return volumeList;
    }

    private List<MountPoint> setupMount(){
        List<MountPoint> mountPointList = new ArrayList<MountPoint>();
        mountPointList.add(newMount(runAlgoName, runAlgoPath, false));
        mountPointList.add(newMount(dataVolumeName, dataPath, true));
        mountPointList.add(newMount(extVolumeName, extPath, false));
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