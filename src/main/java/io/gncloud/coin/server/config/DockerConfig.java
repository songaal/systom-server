package io.gncloud.coin.server.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * create joonwoo 2018. 5. 16.
 * 
 */
@Configuration
public class DockerConfig {

    @Value("${backtest.host}")
    private String dockerHost;

    @Bean
    public DockerClientConfig dockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
    }

    @Bean
    public DockerCmdExecFactory DockerCmdExecFactory(){
        return new JerseyDockerCmdExecFactory().withReadTimeout(10000)
                                               .withConnectTimeout(3000);
    }

    @Bean
    public DockerClient dockerClient(DockerClientConfig dockerClientConfig, DockerCmdExecFactory dockerCmdExecFactory){
        return DockerClientBuilder.getInstance(dockerClientConfig)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
    }
}