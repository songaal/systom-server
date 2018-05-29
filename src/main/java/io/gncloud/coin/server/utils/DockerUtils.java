package io.gncloud.coin.server.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
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
    @Value("${backtest.registry}")
    private String registry;
    @Value("${backtest.container.readTimeout}")
    private int readTimeout;
    @Value("${backtest.container.connectTimeout}")
    private int connTimeout;


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

    public String run(String taskId, String image, List<String> cmd) throws InterruptedException {
        DockerClient dockerClient = getClient();

        CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withCmd(cmd)
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
            backtestLogger.info("[{}] Container ExitCode not 0 {}", taskId, exitCode);
            throw new InterruptedException("ExitCode " + exitCode);
        } else {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            backtestLogger.info("[{}] BackTest Docker Run Finished!", taskId);
        }
//        TODO return taskId
        return containerId;
    }

    class LogContainerTestCallback extends LogContainerResultCallback {
        private org.slf4j.Logger strategyLogger = LoggerFactory.getLogger("strategyLogger");
        private String backtestName;

        LogContainerTestCallback (String backtestName){
            this.backtestName = backtestName;
        }
        @Override
        public void onNext(Frame frame) {
            strategyLogger.info( "[{}] {}", backtestName, new String(frame.getPayload(), Charset.defaultCharset()) );
        }
    }
}