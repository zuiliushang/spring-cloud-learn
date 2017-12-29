package app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;


public class EurekaClientConfig {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	public String serviceUrl() {
		List<ServiceInstance> list = discoveryClient.getInstances("STORES");
		if(list != null && list.size() > 0) {
			return list.get(0).getUri().toString();
		}
		return null;
	}
	
}
