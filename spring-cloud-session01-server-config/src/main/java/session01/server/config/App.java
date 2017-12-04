package session01.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@EnableConfigServer
@SpringBootApplication
@RestController
public class App {
	
	@Value("${name}")
	private String name;
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@GetMapping("name")
	@ResponseBody
	public String get() {
		return name;
	}
	
}

