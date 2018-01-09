## 断路器: Hystrix 客户端

Netflix 已经创建了一个库叫做 Hystrix来实现断路器模式

服务级别较低的服务失败会导致级联失败一直到用户。当调用一个特别的服务超过```circuitBreaker.requestVolumeThreshold```(默认:20次请求)并且失败百分比超过```circuitBreaker.errorThresholdPercentage```(默认>50%)在一个滚动窗口定义通过```metrics.rollingStats.timeInMilliseconds```(默认10秒)断路会打开并且这个调用不会通过。在出现错误的情况下，开发人员可以打开一个断路来回退。

### 如何使用Hystrix

使用group```org.springframework.cloud```和artifact id```spring-cloud-starter-netflix-hystrix```的starter。

例如:
<pre>
@SpringBootApplication
@EnableCircuitBreaker
public class App {

	public static void main(String[] args) {
		new SpringApplicationBuilder(App.class).web(true).run(args);
	}
	
}

@Component
public class StoreIntegration {

	@HystrixCommand(fallbackMethod="defaultStores")
	public Object getStores(Map<String, Object> parameters) {
		// 做一些可能失败的事情
		return null;
	}
	
	
	public Object defaultStores(Map<String, Object> parameters) {
		return null;//返回有用信息
	}
}

</pre>

```@HystrixCommand```提供一个Netflix共享库为"javanica".Spring Cloud自动的包装Spring bean有一个注解来代理连接到Hystrix 断路器。断路器计算什么时候来关闭和打开断路，并且决定关注哪类错误。

配置```@HystrixCommand```你可以使用附带着一系列的```@HystrixProperty```注解的```commandProperties```属性。

### 传播Security Context或者使用Spring Scopes

如果你想要默认声明一些线程本地上下文传播到一个```@HystrixCommand```中将不起作用因为它运行命令在一个线程池中(关注超时)。你可以开关Hystrix使用相同的线程作为调用者来使用一些配置,或者直接的注解通过告诉它来使用一个不一样的"Isolation Strategy"。例如:
<pre>
@HystrixCommand(fallbackMethod="stubMyService",
			commandProperties= {
					@HystrixProperty(name="execution.isolation.strategy",value="SEMAPHORE")
			})
	public Object properties(Map<String, Object> parameters) {
		return null;
	}
	
</pre>
同样支持如果你使用```@SessionScope```或者```@RequestScope```。您将知道何时需要这样做，因为运行时异常表示它不能找到作用域上下文。

你也可以设置```hystrix.shareSecurityContext```为```true```。这样做将自动配置一个Hystrix并发策略插件钩子，它将把SecurityContext从您的主线程转移到Hystrix命令使用的线程。Hystrix不允许多个hystrix并发策略注册所以一个扩展机制是有价值的通过声明你自己的```HystrixConcurrencyStrategy```为Spring bean。Spring Cloud将会查看你的实现类在Spring context中并且包装它为自己的插件。

### 健康指示

断路器的链接状态同样暴露在调用应用的```/health```端点:

<pre>
{
    "hystrix": {
        "openCircuitBreakers": [
            "StoreIntegration::getStoresByLocationLink"
        ],
        "status": "CIRCUIT_OPEN"
    },
    "status": "UP"
}
</pre>

### Hystrix Metrics Stream

开启Hystrix metrics stream需要一个依赖为```spring-boot-starter-actuator```暴露在```/hystrix.stream```端点

<pre>
 <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</pre>

### Hystrix 超时和Ribbon 客户端

当使用Hystrix命令包装Ribbon客户端时，您需要确保您的Hystrix超时被配置为比配置的Ribbon超时时间更长，包括任何可能的重试时间。例如，如果你的Ribbon连接超时是一秒并且会请求重试三次，那么Hystrix超时时间应该是要长于3秒。

#### 使用Hystrix面板

使用Hystrix Dashboard配置group```org.springframework.cloud```和artifact id```spring-cloud-starter-hystrix-netflix-dashboard```。

运行Hystrix Dashboard需要注解```@EnableHystrixDashboard```添加到Spring Boot主类.可以访问```/hystrix```