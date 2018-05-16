package io.gncloud.coin;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
public class DockerTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(DockerTest.class);

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

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(10000)
                .withConnectTimeout(3000);

        dockerClient = DockerClientBuilder.getInstance(config)
                                          .withDockerCmdExecFactory(dockerCmdExecFactory)
                                          .build();
    }

    @Test
    public void info() {
//        도커 정보
        Info info = dockerClient.infoCmd().exec();
        logger.info("info :{}", info);
    }

    @Test
    public void imageSearch() {
//        이미지 검색
        List<SearchItem> dockerSearch = dockerClient.searchImagesCmd("python").exec();
        System.out.println("Search returned" + dockerSearch.toString());
    }

    @Test
    public void createContainer() {
//        컨테이너 생성
//        LogContainerTestCallback loggingCallback = new LogContainerTestCallback(true);
        CreateContainerResponse container = dockerClient.createContainerCmd("busybox")
                                                        .withCmd("touch", "/test")
                                                        .exec();


        //    실행
//
//        dockerClient.startContainerCmd(container.getId()).exec();


    }

    @Test
    public void startContainer() {
//        "6a8b6ecb7080"
//        dockerClient.startContainerCmd("6a8b6ecb7080").exec();
//        dockerClient.stopContainerCmd("6a8b6ecb7080").exec();
//        CountDownLatch latch = new CountDownLatch(1);

        MyResultCallback callback = new MyResultCallback();
        dockerClient.waitContainerCmd("00").exec(callback);




    }

    class MyResultCallback implements ResultCallback<WaitResponse> {

        @Override
        public void onStart(Closeable closeable) {
            logger.info("onStart: {}", closeable);
        }

        @Override
        public void onNext(WaitResponse object) {
            logger.info("onNext {}", object);
        }

        @Override
        public void onError(Throwable throwable) {
            logger.info("onError {}", throwable);
        }

        @Override
        public void onComplete() {
            logger.info("onComplete");
        }

        @Override
        public void close() throws IOException {
            logger.info("close");
        }
    }

}