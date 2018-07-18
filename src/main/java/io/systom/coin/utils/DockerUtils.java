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

    public void syncRun(String taskId, List<String> env, List<String> cmd) throws InterruptedException {
        DockerClient dockerClient = getClient();
        CreateContainerResponse container = dockerClient.createContainerCmd(backTestImage)
                                                        .withEnv(env)
                                                        .withCmd(cmd)
                                                        .exec();

        String containerId = container.getId();
        backtestLogger.info("[{}] Created ContainerId: {}", taskId, containerId);

        dockerClient.startContainerCmd(containerId).exec();
        WaitContainerResultCallback waitContainerResultCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(containerId).exec(waitContainerResultCallback);
        backtestLogger.info("[{}] Start Container", taskId);

        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(taskId);
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
            backtestLogger.info("[{}] BackTest Docker Run Finished!", taskId);
        } else {
            backtestLogger.error("[" + taskId + "] Container ExitCode not zero.. return code: " + exitCode);
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