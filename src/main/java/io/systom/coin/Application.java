package io.systom.coin;

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
@ComponentScan(basePackages = "io.systom.coin")
public class Application {

	public static void main(String[] args) {
		try {
			String isDevTool = System.getenv("devtools");
			if (isDevTool != null && "true".equals(isDevTool)){
				System.setProperty("spring.devtools.restart.enabled", "true");
			} else {
				System.setProperty("spring.devtools.restart.enabled", "false");
			}

			SpringApplication.run(Application.class, args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

}
