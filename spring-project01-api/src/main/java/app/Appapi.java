package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableDiscoveryClient
public class Appapi {

	@Value("${name}")
	private String name;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	@GetMapping("/")
	public String home() {
		String serviceA = restTemplate.getForObject("http://serviceA/", String.class);
		String serviceB = restTemplate.getForObject("http://SERVICEB/", String.class);
		//String serviceA = restTemplate.getForObject("http://say-hello/greeting", responseType)
		return "Hello world Eureka " + name+ "...." +serviceA +"=>"+serviceB;
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(Appapi.class).web(true).run(args);
	}
	
}
