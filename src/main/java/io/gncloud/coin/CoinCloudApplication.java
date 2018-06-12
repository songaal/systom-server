package io.gncloud.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = "io.gncloud.coin")
public class CoinCloudApplication {

	public static void main(String[] args) {
		try {
			System.setProperty("spring.devtools.restart.enabled", "false");
			SpringApplication.run(CoinCloudApplication.class, args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

}
