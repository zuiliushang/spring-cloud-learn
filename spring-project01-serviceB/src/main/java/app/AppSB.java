package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableFeignClients
@EnableHystrix
@EnableHystrixDashboard
public class AppSB {

	@Bean
	@LoadBalanced
	public RestTemplate RestTemplate() {
		return new RestTemplate();
	}
	
	@Autowired
	RestTemplate restTemplate;
	
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
	
	@HystrixCommand(fallbackMethod="dofallback")
	@GetMapping("hello")
	public String hello() {
		return restTemplate.getForObject("http://serviceA/", String.class);
	}
	
	
	public String dofallback() {
		return "sorry hello fallback";
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(AppSB.class).web(true).run(args);
	}
	
}
