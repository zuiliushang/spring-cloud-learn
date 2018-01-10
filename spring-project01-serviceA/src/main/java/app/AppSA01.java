package app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class AppSA01 {

	@Value("${name}")
	private String name;
	
	@Value("${server.port}")
	private String port;
	
	@GetMapping("/")
	public String home() {
		return "Hello world Eureka " + name;
	}
	@GetMapping("/hi")
	public String a1df() {
		return port+"hi";
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(AppSA01.class).web(true).run(args);
	}
	
}
