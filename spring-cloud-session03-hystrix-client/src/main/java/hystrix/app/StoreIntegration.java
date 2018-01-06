package hystrix.app;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Component
public class StoreIntegration {

	@HystrixCommand(fallbackMethod="defaultStores")
	public Object getStores(Map<String, Object> parameters) {
		// 做一些可能失败的事情
		System.out.println(1/0);
		return null;
	}
	
	
	public Object defaultStores(Map<String, Object> parameters) {
		return null;//返回有用信息
	}
	
	@HystrixCommand(fallbackMethod="stubMyService",
			commandProperties= {
					@HystrixProperty(name="execution.isolation.strategy",value="SEMAPHORE")
			})
	public Object properties(Map<String, Object> parameters) {
		return null;
	}
	
}
