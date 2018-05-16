package io.gncloud.coin;

import io.gncloud.coin.server.utils.DockerUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@SpringBootTest(classes = CoinCloudApplication.class)
public class DockerStartTest {

    @Autowired
    private DockerUtils dockerUtils;

    List<String> cmd = Arrays.asList("/bin/sh", "-c", "echo hello");
    int tmpI = 0;
    @Test
    public void startTest() throws InterruptedException {
        for(int i=0; i< 30; i++) {
            tmpI = i;
            dockerUtils.run("test-" + tmpI,"busybox", cmd);
        }

    }

}