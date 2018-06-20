package io.systom.coin.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
@Component
public class DockerUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(DockerUtils.class);

    private static org.slf4j.Logger backtestLogger = LoggerFactory.getLogger("backtestLogger");

    @Value("${backtest.host}")
    private String dockerHost;
    @Value("${backtest.container.readTimeout}")
    private int readTimeout;
    @Value("${backtest.container.connectTimeout}")
    private int connTimeout;
    @Value("${backtest.image}")
    private String backTestImage;
    @Value("${backtest.apiServerHost}")
    private String apiServerHost;
    @Value("${backtest.apiGatewayHost}")
    private String apiGatewayHost;

    private DockerClientConfig config;
    private DockerCmdExecFactory factory;

    @PostConstruct
    private void init() {
        config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                          .withDockerHost(dockerHost)
                                          .build();
        factory = new JerseyDockerCmdExecFactory().withReadTimeout(readTimeout)
                                                  .withConnectTimeout(connTimeout);
    }

    public DockerClient getClient() {
        return DockerClientBuilder.getInstance(config)
                                  .withDockerCmdExecFactory(factory)
                                  .build();
    }

    public void run(int taskId, List<String> envList, List<String> cmd) throws InterruptedException {
        DockerClient dockerClient = getClient();

        envList.add("api_server_host=" + apiServerHost);
        envList.add("api_gateway_host=" + apiGatewayHost);

        Volume hostDataVolume = new Volume("/data");
        Bind gastDataVolume = new Bind("/coinArk/data", hostDataVolume);

        List<Volume> volumeList = new ArrayList<>();
        volumeList.add(hostDataVolume);
        List<Bind> bindList = new ArrayList<>();
        bindList.add(gastDataVolume);

        CreateContainerResponse container = dockerClient.createContainerCmd(backTestImage)
                                                        .withEnv(envList)
                                                        .withCmd(cmd)
                                                        .withVolumes(volumeList)
                                                        .withBinds(bindList)
                                                        .exec();

        String containerId = container.getId();
        backtestLogger.info("[{}] Created ContainerId: {}", taskId, containerId);

        dockerClient.startContainerCmd(containerId).exec();
        WaitContainerResultCallback waitContainerResultCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(containerId).exec(waitContainerResultCallback);
        backtestLogger.info("[{}] Start Container", taskId);

        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(containerId);
        dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .withFollowStream(true)
                .withTailAll()
                .exec(loggingCallback);

        loggingCallback.awaitCompletion();

        int exitCode = waitContainerResultCallback.awaitStatusCode();
        if (exitCode != 0) {
            backtestLogger.error("[" + taskId + "] Container ExitCode not zero.. return code: " + exitCode);
            throw new InterruptedException("BackTest Running Fail");
        } else {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            backtestLogger.info("[{}] BackTest Docker Run Finished!", taskId);
        }
    }

    class LogContainerTestCallback extends LogContainerResultCallback {
        private org.slf4j.Logger strategyLogger = LoggerFactory.getLogger("strategyLogger");
        private String backtestName;

        LogContainerTestCallback (String backtestName){
            this.backtestName = backtestName.substring(10);
        }
        @Override
        public void onNext(Frame frame) {
            strategyLogger.info( "[{}] {}", backtestName, new String(frame.getPayload(), Charset.defaultCharset()) );
        }
    }
}