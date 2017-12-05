# Spring Cloud Netflix

**1.3.5.BUILD-SHAPSHOT**

这个项目提供了 Netflix OSS集成到Spring Boot 应用通过自动配置和绑定到Spring Environment和其他Spring程序模块风格。使用很少简单的注解你可以快速启动和配置常用模式在的你应用中并且构建一个久经沙场的Netflix组件的大型分布式系统。这个模式提供包括Service Discovery(Eureka),Circuit Breaker(Hystrix),Intelligent Routing(Zuul)和Client Side Load Balancing (Ribbon);

## 服务发现:Eureka客户端

服务发现是微服务的一个关键基础功能。尝试分发配置到每个客户端或者一些交流的表单很难做，而且可能很脆弱。Eureka是Netflix 服务发现服务器和客户端。服务器可以被配置在多复制状态发布得高可用来提供注册服务。

## 如何包含Eureka客户端

使用group```org.springframework.cloud```和artifact id```spring-cloud-stater-netflix-eureka-client```。

## Eureka上注册

当一个客户端在Eureka上注册，它提供自身host和port,health indicator URL，home Page等元数据。Eureka接受每个服务的实例的心跳信息。如果心跳停止超过配置时间，这个实力会被正常的移除出注册。

例如一个eureka client:

<pre>
@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class App01 {

	@GetMapping("/")
	public String home() {
		return "Hello world Eureka";
	}
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(App01.class).web(true).run(args);
	}
	
}

</pre>

(一个完全正常的Spring Boot应用).通过有一个```spring-cloud-starter-netflix-eureka-client```在应用的classpath中会自动的注册到Eureka Server。配置Eureka服务器需要配置。例如：

<pre>
eureka:
 client:
  serviceUrl:
# defaultZone提供这个服务的地址
   defaultZone: http://localhost:8081/eureka/
</pre>

"defaultZone"是一个虚拟的回调字符串值提供服务任何客户端的URL并不表达偏好(默认可用)。

默认应用名称(service ID),虚拟地址和非安全的端口，都存入到```Environment```，并且为```${spring.application.name}```,```${spring.application.name}```和```${server.port}```分开。

有一个```spring-cloud-starter-netflix-eureka-client```在classpath中让应用等同为一个Eureka"实例"(即自我注册)并且一个"客户端"（也就是说，它可以查询注册表来定位其他服务）。实例的举动通过```eureka.instance*```配置key来驱动，但是默认如果你确保你的应用有一个```spring.application.name```(这个默认是Eureka服务ID或者VIP)会更好。

为了禁用Eureka Discovery Client你可以设置```eureka.client.enabled```为```false```

## Eureka Server 权限

HTTP基础权限自动添加到你的eureka客户端如果```eureka.client.serviceUrl.defaultZone```URL其中一个嵌入了凭证(curl风格,像```http://user:password@localhost:8761/eureka```).更多复杂需要你创建一个```DiscoveryClientOptionalArgs```类型的```@Bean```并且注入到```ClientFilter```实例中区。这些全部都会被支持调用从客户端到服务器。

> 由于Eureka的限制，因此不可能支持每个服务器的基本auth凭证，所以只有第一个被发现的集合将被使用。

## 状态页面和健康指示器

一个Eureka实例的状态页健康指示器默认分别在"/info"和"/health"，这个使用端点路径在Spring Boot Actuator 应用中默认。你需要改变它甚至一个Actuator应用如果你使用非默认的上下文路径或者servlet路径(例如```server.servletPath=/foo```)或者管理端点路径(例如```management.contextPath=/admin```).例如：
application.yml

<pre>
management:
 context-path: /admin

eureka:
 instance:
  statusPageUrlPath: ${management.context-path}/info
  healthCheckUrlPath: ${management.context-path}/health
</pre>

这些链接展示在元数据并被客户端消费并且使用在一些场景来决定是否要发送请求到你的应用，所以如果他们很精确那么非常有帮助。

## 注册一个安全的应用

如果你的应用想要通过HTTPS交流你需要设置两个标志在```EurekaInstanceConfig```，即分别设置```eureka.instance.[nonSecurePortEnabled,securePortEnabled]=[false,true]```。这个将会让Eureka发布实例信息展示在一个明确的引用来加密交流。Spring Cloud```DiscoveryClient```总会返回一个URI开头为```https```关于