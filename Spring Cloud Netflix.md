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

如果你的应用想要通过HTTPS交流你需要设置两个标志在```EurekaInstanceConfig```，即分别设置```eureka.instance.[nonSecurePortEnabled,securePortEnabled]=[false,true]```。这个将会让Eureka发布实例信息展示在一个明确的引用来加密交流。Spring Cloud```DiscoveryClient```总会返回一个URI开头为```https```关于一个服务配置，和Eureka(本地)实例信息将会有一个健康检查URL。

因为这种方式的Eureka工作在内部，它仍会公开一个不安全的URL关于状态和首页除非你也明确地重写这些。你可以使用占位符来配置eureka实例url，比如:
application.yml
<pre>
eureka:
 instance:
  statusPageUrl: https://${eureka.hostname}/info
  healthCheckUrl: https://${eureka.hostname}/health
  homePageUrl: https://${eureka.hostname}/
</pre>

(注意```${eureka.hostname}```)最后一个版本只有一个占位符。你可以获得同样的Spring 占位符，例如使用```${eureka.instance.hostName}```.)

> 如果你的应用运行在一个代理和SSL代理(例如如果你运行在Cloud Foundry或者其它Paas)你将需要确认代理"传递"头被应用拦截并且处理。一个嵌入式的Tomcat容器在Spring Boot应用自动化做这个如果它有明确的'X-Forwarded-\*'配置头。你有错的一个信号是，你的应用程序所呈现的链接将是错误的(错误的主机、端口或协议)。

## Eureka Health Check

默认,Eureka使用客户端心跳来决定一个客户端注册。除非指定否则Discovery客户端不会在Spring引导执行器中传播当前应用程序的健康检查状态。这意味着在成功注册之后Eureka总是把应用记录为'UP'状态。这个举动可以通过启动Eureka健康检查来改变，结果将应用程序状态传递给Eureka。因此，其他应用程序都不会将流量发送到其他状态的应用程序。

application.yml
<pre>
eureka:
 client:
  healthcheck:
   enabled: true
</pre>

> ```eureka.client.healthcheck.enabled=true```只能被这只在```application.yml```.设置这个值到```bootstrap.yml```造成不良的副作用例如eureka变成```UNKNOWN```状态。

如果你需要更多的通知健康检查，你可以自定义实现你自己的```com.netfix.appinfo.HealthCheckHandler```.


## Eureka实例和客户端的Metadata

非常值得花费大量时间来理解Eureka metadata是如何工作的，这样你可以使用它们在你的平台中。这里有一些标准的元数据例如hostname，IP address,port numbers,status page和 health check.它们在服务注册的使用公开和背客户端使用通过很直接的方式。额外的metadata可以被添加在实例中注册```eureka.instance.metadataMap```，并且这会被接受到启动客户端里，但是正常不会改变客户端的举动，除非它知道元数据的含义。下面描述了一些特殊情况，其中Spring云已经为元数据映射分配了意义。

### 在Cloudfoundry中使用Eureka

Cloudfoundry有一个全局的路由所以所有的相同的实例应用有相同的hostname(这个在其他PaaS中有一个类似的结构解决)。这个不接近于一个使用Eureka的屏障，但是如果你使用路由(推荐，甚至是强制性的，这取决于平台设置的方式)，你需要明确的设置hostname和port(安全或者非安全)来使用路由。你可能也想要使用实例元数据你可以在客户端之间的实例区分它们（例如在定制的负载均衡）。默认是```eureka.instance.instanceId```是```vcap.application.instance_id```。例如：

application.yml
<pre>
eureka:
 instance:
  hostname: ${vcap.application.uris[0]}
  nonSecurePort: 80
</pre>

使用安全规则的方式在你的Cloudfoundry实例中创建Eureka。你可以注册并且使用虚拟主机的IP地址来直接的服务之间的调用。这个功能在关键的Web服务(Pivotal Web Services PWS)不是很高效>

### 在AWS中使用Eureka

如果应用计划发布在一个AWS云上，那么Eureka实例将被配置为能被AWS识别到这个可以通过定制```EurekaInstanceConfigBean```如下：

<pre>
	@Bean
	@Profile("!default")
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
		EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
		AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
		b.setDataCenterInfo(info);
		return b;
	}
</pre>

### 改变Eureka实例ID

一个ID注册一个Netflix Eureka实例等同于它的host名(即一个主机一个服务)。Spring Cloud Eureka提供一个默认发现看起来像:```${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}```.例如:
```myhost:myappname:8080```

使用Spring Cloud你可以重写这个通过提供一个唯一的身份在```eureka.instance.instanceId```，例如：

application.yml

<pre>
eureka:
 instance:
  instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.applicationinstance_id:${random.value}}}
</pre>


在多服务器实例本地发布下,有了这个元数据，一个随机值会设入到这个实例保持唯一。在Cloudfoundry中```vcap.application.instance_id```将会自动生成在一个Spring Boot 应用。所以随机值并不需要。

## 使用 EurekaClient

一旦你有一个发现客户端的app你可以使用它来发现服务实例通过Eureka Server。一种方式是使用本地```com.netfix.discovery.EurekaClient```（而不是Spring Cloud```DiscoveryClient```），例如：
<pre>
	public String serviceUrl() {
		InstanceInfo instance = discoveryClient.getNextServerFromEureka("token", false);
		return instance.getHomePageUrl();
	}
</pre>

> 不要在```@Scheduled```方法或者任何在```ApplicationContext```还没有开始的地方使用```EurekaClient```。它在```SmartLifecycle```中初始化(```phase=0```)所以你最早可以依赖的是在另一个更高阶段的```SmartLifecycle```。

### EurekaClient不适用Jersey

默认,EurekaClient使用Jersey来进行HTTP交流。如果想要忽略Jersey依赖，可以包含来你的依赖,Spring Cloud会自动配置一个传输客户端在Spring的```RestTemplate```。

<pre>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey.contribs</groupId>
            <artifactId>jersey-apache-client4</artifactId>
        </exclusion>
    </exclusions>
</dependency>
</pre>

### 替代本地的Netflix EurekaClient

你不需要使用Netfix```EurekaClient```规则并且通常它更加方便使用来包装一些东西。Spring Cloud已经支持Feign(一个REST客户端构建者)和Spring ```RestTemplate```使用逻辑上的Eureka服务身份(VIPs)来带图物理上的URLs。通过用固定的物理服务器列表配置Ribbon你可以简单设置```<client>.ribbon.listOfServers```到一个逗号分隔的物理地址(或主机)列表中，```<client>```是客户端的ID。

你也可以使用```org.springframework.cloud.client.dicovery.DiscoveryClient```来提供一个简单API来发现客户端而没有特定的Netflix,如：
<pre>
@Autowired
	private DiscoveryClient discoveryClient;
	
	public String serviceUrl() {
		List<ServiceInstance> list = discoveryClient.getInstances("STORES");
		if(list != null && list.size() > 0) {
			return list.get(0).getUri().toString();
		}
		return null;
	}
	
</pre>

### 注册一个服务很慢的原因

形成一个实例也需要调用一个定期性的心跳到注册(通过客户端的```serviceUrl```)默认是30秒。在实例之前，客户机无法发现服务。服务和客户端都有相同的元数据在它们本地缓存(所以可能需要3次心跳)。你可以改变这个周期通过使用```eureka.instance.leaseRenewalIntervalInSeconds```然后这个将会加速处理获取客户端连接到其他服务中。在生产中，最好还是保留默认值，因为服务器内部有一些计算租约更新期的假设。

### Zones

如果你已经发布一个Eureka客户端到大量的zone您可能希望这些客户端在同一区域内使用服务，然后再在另一个区域中尝试服务。要做到这一点，您需要正确地配置您的Eureka客户端。

首先，需要确定在各个zone中有Eureka服务发布，然后他们彼此通讯。

然后你需要告诉Eureka哪些zone你的服务在里面。你可以使用```metadataMap```属性做到。例如如果```service 1```发布在```zone 1```和```zone 2```你将需要设置关于Eureka属性到```service 1```

#### Service 1 在 Zone 1
<pre>
instance:
  matadataMap:
   zone: zone1
 client:
  preferSameZoneEureka: true
</pre>

#### Service 2 在 Zone 2
<pre>
 instance:
  matadataMap:
   zone: zone2
 client:
  preferSameZoneEureka: true
</pre>

## 服务发现：Eureka Server

### 如何包含Eureka服务

包含服务到你的项目中使用starter group为```org.spring.framework.cloud```和artifact id```spring-cloud-starter-netflix-eureka-server```。

### 如何运行一个Eureka服务器

例子：

<pre>
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EurekaServerApp.class).web(true).run(args);
	}
	
}

</pre>

服务器有一个主页UI，以及在```/eureka/*```下正常的Eureka功能的HTTP API端点。

### 高可用,Zone和Region

Eureka服务器没有一个后端存储，但是所有在注册中的服务实例都会发送时间心跳来保持它们的注册状态（所以这个可能在内存中完成）。客户端当然有一个记忆缓存eureka注册信息（所以他们不必要通过注册表来发送任何请求到服务）。默认每个Eureka服务器也是一个Eureka客户端并且需要(至少一个)服务URL到本地对。如果你没有提供它服务将会运行和工作，但是它将会让你的logs一直报错没有注册好一个对。

### 独立模式

两个缓存(client和server)的缓存和心跳组成一个独立的能弹性解决失败的Eureka服务器，等同于一些监控或者弹性运行保持工作(例如 Cloud Foundry)。在独立模式中，你可能更倾向于关闭客户端的激动，因此它不会继续尝试，也不会到达它的同类。例子:

application.yml(独立模式的 Eureka Server)。
<pre>
server:
 port: 8080

eureka:
 instance:
  hostname: localhost
 client:
  registerWithEureka: false
  fetchRegistry: false
  serviceUrl:
   defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
</pre>

注意```serviceUrl```标注相同的主机作为本地实例。

### 对等意识

Eureka可以做得更有弹性和有用通过运行大量的实例并且注册到彼此。事实上，这个一个默认的举动，所以所有你需要让它工作只需要添加一个有效的```serviceUrl```作为一个对，例如:

application.yml (两个对等意识的Eureka Servers)
<pre>
---
spring:
 profiles: peer1
eureka:
 instance:
  hostname: peer1
 client:
  serviceUrl:
   defaultZone: http://peer2/eureka/

---
spring:
 profiles: peer2
eureka:
 instance:
  hostname: peer2
 client:
  serviceUrl:
   defaultZone: http://peer1/eureka/
</pre>

在这个例子我们有一个YAML文件可以用来运行相同的服务到两个主机中(peer1和peer2)，通过运行它到不同的Spring profile，你可以使用这个配置来测试对等意识在一个host（没有匹配值在生产中）通过模拟```/etc/host```来解决主机名。事实上，```eureka.instance.hostname```不需要如果你运行在一个机器,这个机器知道自己的主机名(可以使用```java.net.InetAddress```默认)

你可以添加大量的对在系统，并且同样他们的所有会连接彼此通过至少一个边缘，他们总会自我同步注册信息。如果有一对物理上分割（在一个数据中心或者在多个数据中心之间）系统原则上来说，“裂脑”式的失败是存在的。

### 使用IP地址

在一些情节，对Eureka来说更好的方式是建议IP地址的服务方式比主机名更好。设置```eureka.instance.preferIpaddress```为```true```然后当应用注册到eureka，它将会使用它的IP地址而不是它的主机名。

> 如果hostname不能通过Java决定，那么IP地址发送给Eureka。只有使用了明确的方式设置主机名通过```eureka.instance.hostname```。你可以设置你的hostname在运行时间使用环境变量。例如```eureka.instance.hostname=${HOST_NAME}```。