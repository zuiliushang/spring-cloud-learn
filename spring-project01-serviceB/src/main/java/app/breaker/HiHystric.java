package app.breaker;

import org.springframework.stereotype.Component;

import app.SchedualServiceHi;

@Component
public class HiHystric implements SchedualServiceHi{

	/**
	 * 断路器开启的返回
	 */
	@Override
	public String sayHiFromClientOne(String name) {
		return "I am fallback sorry";
	}

}
