package io.systom.coin.aws;

import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import com.google.gson.Gson;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.CoinSignal;
import io.systom.coin.model.DualSignal;
import io.systom.coin.model.Network;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private AmazonEC2 ec2Client;

    @Before
    public void setup(){
         client = AmazonECSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
        ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
    }

    @Test
    public void executorRequest() {
        String taskId = "arn:aws:ecs:ap-northeast-2:868448630378:task/77153044-79b8-42ae-ade9-80f18db23db6";
        CoinSignal coinSignal = new CoinSignal();
        coinSignal.setAction("SLD");
        coinSignal.setSymbol("FUN/BTC");
        coinSignal.setType("SIGNAL");
        coinSignal.setWeight(1f);
        CoinSignal.Reason coinReason = new CoinSignal.Reason();
        coinReason.setAuthor("joonwoo");
        coinReason.setMessage("testtest");
        coinSignal.setReason(coinReason);
        coinSignal.setTime((int) new Date().getTime());

        CoinSignal baseSignal = new CoinSignal();
        baseSignal.setAction("SLD");
        baseSignal.setSymbol("BTC/USDT");
        baseSignal.setType("SIGNAL");
        baseSignal.setWeight(1f);
        CoinSignal.Reason baseReason = new CoinSignal.Reason();
        baseReason.setAuthor("joonwoo");
        baseReason.setMessage("basetestestset");
        baseSignal.setReason(baseReason);
        baseSignal.setTime((int) new Date().getTime());

        DualSignal dualSignal = new DualSignal();
        dualSignal.setType("SIGNAL");
        dualSignal.setTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        dualSignal.setCoin_signal(coinSignal);
        dualSignal.setBase_signal(baseSignal);
        ResponseEntity<String> response = executorSignal(taskId, dualSignal);
        logger.debug("{}", response.getBody());


    }
    public ResponseEntity<String> executorSignal(String ecsTaskId, CoinSignal coinSignal) {
        String requestBody = new Gson().toJson(coinSignal);
        return executorRequest(ecsTaskId, requestBody);
    }
    public ResponseEntity<String> executorSignal(String ecsTaskId, DualSignal dualSignal) {
        String requestBody = new Gson().toJson(dualSignal);
        return executorRequest(ecsTaskId, requestBody);
    }

    protected ResponseEntity<String> executorRequest(String ecsTaskId, String requestBody) {
        Network network = getTaskHostNetwork(ecsTaskId);
        if (network == null) {
            throw new OperationException("Not Found Task Container Network");
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("http://%s:%d/signal", network.getIp(), network.getPort());
        logger.debug("url: {}", url);
        return restTemplate.postForEntity(url, requestBody, String.class);
    }

    protected Network getTaskHostNetwork(String ecsTaskId) {
        DescribeTasksRequest describeTasksRequest = new DescribeTasksRequest();
        describeTasksRequest.withTasks(ecsTaskId);
        describeTasksRequest.withCluster(clusterId);
        DescribeTasksResult describeTasksResult =  client.describeTasks(describeTasksRequest);

        Integer port = null;
        String containerInstanceId = null;
        for (Container container: describeTasksResult.getTasks().get(0).getContainers()) {
            if ("systom-executor".equals(container.getName())) {
                port = container.getNetworkBindings().get(0).getHostPort();
                containerInstanceId = describeTasksResult.getTasks().get(0).getContainerInstanceArn();
            }
        }

        if (port == null || containerInstanceId == null) {
            return null;
        }
        logger.debug("executor request containerInstanceId: {}, port: {}", containerInstanceId, containerInstanceId);

        DescribeContainerInstancesRequest describeContainerInstancesRequest = new DescribeContainerInstancesRequest();
        describeContainerInstancesRequest.withCluster(clusterId);
        describeContainerInstancesRequest.withContainerInstances(containerInstanceId);
        DescribeContainerInstancesResult describeContainerInstancesResult = client.describeContainerInstances(describeContainerInstancesRequest);
        String instanceId = describeContainerInstancesResult.getContainerInstances().get(0).getEc2InstanceId();

        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        describeInstancesRequest.withInstanceIds(instanceId);
        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances(describeInstancesRequest);
        String ip = describeInstancesResult.getReservations().get(0).getInstances().get(0).getPublicIpAddress();
        Network network = new Network();
        network.setIp(ip);
        network.setPort(port);

        return network;
    }



    @Test
    public void getPort() {
        String taskId = "arn:aws:ecs:ap-northeast-2:868448630378:task/77153044-79b8-42ae-ade9-80f18db23db6";


        DescribeTasksRequest describeTasksRequest = new DescribeTasksRequest();
        describeTasksRequest.withTasks(taskId);
        describeTasksRequest.withCluster(clusterId);
        DescribeTasksResult describeTasksResult = client.describeTasks(describeTasksRequest);
        logger.debug("{}", describeTasksResult);
        int port = describeTasksResult.getTasks().get(0).getContainers().get(1).getNetworkBindings().get(0).getHostPort();
        String containerInstance = describeTasksResult.getTasks().get(0).getContainerInstanceArn();



//        DescribeContainerInstancesRequest describeContainerInstancesRequest = new DescribeContainerInstancesRequest();
//        describeContainerInstancesRequest.withCluster(clusterId);
//        describeContainerInstancesRequest.withContainerInstances(containerInstance);
//        DescribeContainerInstancesResult describeContainerInstancesResult = client.describeContainerInstances(describeContainerInstancesRequest);
//        logger.debug("{}", describeContainerInstancesResult);
//        String instanceId = describeContainerInstancesResult.getContainerInstances().get(0).getEc2InstanceId();


        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        describeInstancesRequest.withInstanceIds(containerInstance);
        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances(describeInstancesRequest);
        logger.debug("{}", describeInstancesResult);
        String id = describeInstancesResult.getReservations().get(0).getInstances().get(0).getPublicIpAddress();


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