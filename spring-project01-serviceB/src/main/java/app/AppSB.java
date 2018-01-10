package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableFeignClients
public class AppSB {

	@Autowired
	SchedualServiceHi schedualServiceHi;
	
	@Value("${name}")
	private String name;
	
	@GetMapping("/hi")
	public String hi() {
		return schedualServiceHi.sayHiFromClientOne("xsu");
	}
	@GetMapping("/")
	public String home() {
		return "Hello world Eureka " + name;
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(AppSB.class).web(true).run(args);
	}
	
}
