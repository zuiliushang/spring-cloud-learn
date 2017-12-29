package app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaClient
@EnableEurekaServer
@SpringBootApplication
public class StandaloneModelApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(StandaloneModelApp.class).web(true).run(args);
	}
	
}
