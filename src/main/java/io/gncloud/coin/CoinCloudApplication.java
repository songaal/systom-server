package io.gncloud.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "io.gncloud.coin")
public class CoinCloudApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(CoinCloudApplication.class, args);
		} catch (Throwable t) {
			System.err.println(t);
		}
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

}
