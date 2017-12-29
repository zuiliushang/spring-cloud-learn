package app;

import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

public class EurekaClientTest {

	@Autowired
	EurekaClient discoveryClient;
	
	public String serviceUrl() {
		InstanceInfo instance = discoveryClient.getNextServerFromEureka("token", false);
		return instance.getHomePageUrl();
	}
	
}
