package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class App01 {

	@Value("${name}")
	private String name;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	@GetMapping("/")
	public String home() {
		String serviceA = restTemplate.getForObject("http://localhost:8084/", String.class);
		//String serviceA = restTemplate.getForObject("http://say-hello/greeting", responseType)
		return "Hello world Eureka " + name +serviceA;
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(App01.class).web(true).run(args);
	}
	
}
