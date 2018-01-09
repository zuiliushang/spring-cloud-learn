package app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
public class App {

	public static void main(String[] args) {
		new SpringApplicationBuilder(App.class).web(true).run(args);
	}
	
}
