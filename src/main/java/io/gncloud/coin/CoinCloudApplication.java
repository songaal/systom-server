package io.gncloud.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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

}
