package io.systom.coin;

import com.github.dockerjava.api.model.Image;
import io.systom.coin.utils.DockerUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class DockerStartTest {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(DockerStartTest.class);
    @Autowired
    private DockerUtils dockerUtils;

    List<String> cmd = Arrays.asList("python", "run.py");
    int tmpI = 0;
    @Test
    public void startTest() {
//        try {
////            dockerUtils.run("test1","868448630378.dkr.ecr.ap-northeast-2.amazonaws.com/cctrader:latest", cmd);
//        }catch (InterruptedException e) {
//            logger.error("", e);
//        }
    }

    @Test
    public void listImages() {
        dockerUtils.getClient().authConfig().withRegistrytoken("");

        List<Image> images = dockerUtils.getClient().listImagesCmd().exec();
        for(int i=0; i < images.size(); i++) {
            logger.info("[{}] {}", i, images.get(i).getRepoTags());
        }
    }
}