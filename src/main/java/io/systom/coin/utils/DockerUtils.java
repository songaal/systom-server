package io.systom.coin.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import io.systom.coin.model.TraderTask;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
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
    @Value("${backtest.apiServerUrl}")
    private String apiServerUrl;

    @Value("${invest.start.hour}")
    private String startHour;
    @Value("${invest.start.minute}")
    private String startMinute;
    @Value("${invest.start.second}")
    private String startSecond;
    @Value("${invest.end.hour}")
    private String endHour;
    @Value("${invest.end.minute}")
    private String endMinute;
    @Value("${invest.end.second}")
    private String endSecond;

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

    public void syncRun(TraderTask traderTask) throws InterruptedException {
        String startTime = String.format("%s:%s:%s", startHour, startMinute, startSecond);
        String endTime = String.format("%s:%s:%s", endHour, endMinute, endSecond);

        List<String> cmd = traderTask.getBackTestCmd(startTime, endTime);
        cmd.add("api_server_url=" + apiServerUrl);

        List<String> env = traderTask.getBackTestEnv();

        DockerClient dockerClient = getClient();
        CreateContainerResponse container = dockerClient.createContainerCmd(backTestImage)
                                                        .withCmd(cmd)
                                                        .withEnv(env)
                                                        .exec();

        String containerId = container.getId();
        backtestLogger.info("[{}] Created ContainerId: {}", traderTask.getId(), containerId);

        dockerClient.startContainerCmd(containerId).exec();
        WaitContainerResultCallback waitContainerResultCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(containerId).exec(waitContainerResultCallback);
        backtestLogger.info("[{}] Start Container", traderTask.getId());

        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(traderTask.getId());
        dockerClient.logContainerCmd(containerId)
                .withStdErr(true)
                .withStdOut(true)
                .withFollowStream(true)
                .withTailAll()
                .exec(loggingCallback);

        loggingCallback.awaitCompletion();

        int exitCode = waitContainerResultCallback.awaitStatusCode();
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (NotFoundException e) {
            logger.debug("Docker Container Remove Fail. Container Id: {}", containerId);
        }

        if (exitCode == 0) {
            backtestLogger.info("[{}] BackTest Docker Run Finished!", traderTask.getId());
        } else {
            backtestLogger.error("[" + traderTask.getId() + "] Container ExitCode not zero.. return code: " + exitCode);
            throw new InterruptedException("BackTest Running Fail");
        }
    }

    class LogContainerTestCallback extends LogContainerResultCallback {
        private org.slf4j.Logger strategyLogger = LoggerFactory.getLogger("strategyLogger");
        private String taskId;

        LogContainerTestCallback (String taskId){
            this.taskId = taskId.substring(0, 8);
        }
        @Override
        public void onNext(Frame frame) {
            strategyLogger.info( "[{}] {}", taskId, new String(frame.getPayload(), Charset.defaultCharset()) );
        }
    }
}