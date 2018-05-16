package io.gncloud.coin.server.utils;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
@Component
public class DockerUtils {

    @Autowired
    private DockerClient dockerClient;

    public void dockerInfo(){
//        dockerClient
    }

}