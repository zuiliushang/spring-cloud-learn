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
public class App01 {

	@Value("${name}")
	private String name;
	
	@GetMapping("/")
	public String home() {
		return "Hello world Eureka " + name;
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(App01.class).web(true).run(args);
	}
	
}
