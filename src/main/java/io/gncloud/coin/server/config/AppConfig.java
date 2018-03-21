package io.gncloud.coin.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    PropertiesConfiguration propertiesConfiguration() throws Exception {

        PropertiesConfiguration configuration = new PropertiesConfiguration();

        configuration.setReloadingStrategy(new FileChangedReloadingStrategy());

        return configuration;
    }
}