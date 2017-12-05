package app;

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
public class App01 {

	@GetMapping("/")
	public String home() {
		return "Hello world Eureka";
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(App01.class).web(true).run(args);
	}
	
}
