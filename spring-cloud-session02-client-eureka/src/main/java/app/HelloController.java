package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

	private final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	/*@Autowired
	private EurekaRegistration client;
	
	@GetMapping("/hello")
	public String index() {
		Registration instance ;
		logger.info("/hello, host: {} ,service_id: {}",instance.getHost(),instance.getServiceId());
		return "hello";
	}*/
	
}
