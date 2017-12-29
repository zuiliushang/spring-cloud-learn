package app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaClient
@EnableEurekaServer
public class AwarenessEurekaApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AwarenessEurekaApp.class).web(true).run(args);
	}
	
}
