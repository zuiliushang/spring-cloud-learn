package session01.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class Controller {

	@Value("${name}")
	private String name;
	
	@GetMapping("name")
	@ResponseBody
	public String get() {
		return name;
	}
	
	
}
