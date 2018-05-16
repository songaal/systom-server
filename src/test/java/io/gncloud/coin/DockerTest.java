package io.gncloud.coin;

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
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
public class DockerTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(DockerTest.class);

//    private String dockerHost = "tcp://52.79.171.130:2376";
    private String dockerHost = "tcp://52.79.171.130:2376";

    private DockerClient dockerClient;
/*
   참고
   https://github.com/docker-java/docker-java/wiki
*/

    @Before
    public void init () {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory();
//                .withReadTimeout(10000)
//                .withConnectTimeout(3000);

        dockerClient = DockerClientBuilder.getInstance(config)
                                          .withDockerCmdExecFactory(dockerCmdExecFactory)
                                          .build();

    }

    @Test
    public void asyncLogContainerWithTtyEnabled() throws Exception {

        CreateContainerResponse container = dockerClient.createContainerCmd("busybox")
                .withCmd("/bin/sh", "-c", "echo hello")
                .exec();

        logger.info("Created container: {}", container.toString());
        dockerClient.startContainerCmd(container.getId()).exec();

        WaitContainerResultCallback waitContainerResultCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(container.getId()).exec(waitContainerResultCallback);
        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(true);

        dockerClient.logContainerCmd(container.getId())
                .withStdErr(true)
                .withStdOut(true)
                .withFollowStream(true)
                .withTailAll()
                .exec(loggingCallback);

        loggingCallback.awaitCompletion();
        int exitCode = waitContainerResultCallback.awaitStatusCode();

        logger.info("컨테이너 종료 코드: {}, {}",  exitCode, loggingCallback.getCollectedFrames().toString());

    }

    public class LogContainerTestCallback extends LogContainerResultCallback {
        protected final StringBuffer log = new StringBuffer();

        List<Frame> collectedFrames = new ArrayList<Frame>();

        boolean collectFrames = false;

        public LogContainerTestCallback() {
            this(false);
        }

        public LogContainerTestCallback(boolean collectFrames) {
            this.collectFrames = collectFrames;
        }

        @Override
        public void onNext(Frame frame) {
            if (collectFrames) collectedFrames.add(frame);
            log.append(new String(frame.getPayload()));
        }

        @Override
        public String toString() {
            return log.toString();
        }

        public List<Frame> getCollectedFrames() {
            return collectedFrames;
        }
    }
}