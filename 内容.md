# Spring Cloud

版本:Dalston.SR3

Spring Cloud是一个快速构建分布式系统中常见模式的工具(例如配置管理，服务发现，断路器，智能路由，微代理，控制总线)。并协调分布式系统boiler plate patterns。使用Spring Cloud的开发人员可以快速创建服务和应用。并且在任何的分布式系统中良好工作，包括开发人员的电脑，数据中心，和管理平台如Cloud Foundry。

## 功能

Spring Cloud 致力于常见情况使用经验和扩展机制来提供良好的决策体验(就是一套方案)。

- 分布式/版本化配置
- 服务注册和发现
- 路由
- 服务到服务调用
- 读取平衡
- 断路器
- 分布式信息

## Cloud Native 应用

Cloud Native 是一种应用发展风格,这种风格更容易适应持续生产和值驱动发展的最佳实践。一个相关的学科是构建12-factor Apps，其中的开发实践与交付和运营目标一致。例如使用声明式编程和管理和监控。Spring Cloud促进这些发展风格通过多种特殊方式并且初始是一系列所有在分布式系统这种被需要或者不被需要的组件的功能。(靠 应该就是说Spring Cloud其实就是注入的一系列功能的bean)。

这些功能在Spring Boot(Spring Cloud的基础)中被覆盖。大部分已经被交付通过Spring Cloud作为两个类库:Spring Cloud Context和Spring Cloud Common。Spring Cloud Context 提供工具和特别的服务为了让```ApplicationContext```实现配置一个Spring Cloud应用的上下文(启动上下文，加密，作用域刷新和环境endpoint)。Spring Cloud Commons 是一组抽象和普通的类使用在不同的Spring Cloud实现(如 Spring Cloud Netflix Spring Cloud Consul)中。

如果你捕获到一个异常叫做"Illegal key size"并且你用的是Sun的JDK，你需要安装Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.

### Spring Cloud Context: Application Context Services

Spring Boot有一个既定的图纸如何通过Spring构建一个应用:例如它有基础配置文件固定位置。并且通常管理和监控任务。Spring Cloud在最上层添加一些功能可能所有的系统中的组件会用到或者可能需要到。

#### BootStrap Application Context

一个Spring Cloud应用通过创建一个启动上下文来操作，这个上下文是主程序的父上下文。它负责通过外来资源来读取配置属性。并且在本地额外配置文件解密属性。两个上下文共享一个Environment(任何Spring应用的额外属性存放地方)。启动属性被添加在高优先级中，所以他们默认不能在本地配置中被重写；

启动上下文使用一个不同于主应用上下文的定位额外配置的约定。所以可以使用bootstrap.yml而不是用application.yml来保持额外的配置来提供启动和主上下文很好的分离。(就是让你把springcloud的优先级高的配置扔在bootstrap.yml不要扔在application.yml)如：

bootstrap.yml
<pre>
spring:
 application:
  name: foo
 cloud:
  config:
   uri: ${SPRING_CONFIG_URI:http://localhost:8888}
</pre>

一个好主意是设置```spring.application.name```(放在bootstrap.yml或者application.yml)如果你的应用需要来自服务器的任何特定于应用程序的配置。

你可以完整关闭启动通道通过设置```spring.cloud.bootstrap.enabled=false```

#### 应用上下文调层次

如果你通过```SpringApplication```或```SpringApplicationBuilder```创建的引用上下文。那么启动上下文将会添加在这些上下文之上。子上下文会继承到启动上下文的属性。相较于没有Spring Cloud配置的上下文。额外的属性有：

- ```bootstrap```：如果有一个```PropertySourceLocators```在启动上下文被发现那么这个配置就会被```CompositeProertySource```最优先发现，并且为非空的属性。

- ```applicationConfig```:[classpath:bootstrap.yml]\(友好如果一个spring文件激活\)。如果你有一个bootstrap.yml(或properties)然后这些属性将会被用来配置启动上下文。并且他们加入子上下文当它们父上下文被设置。它们的优先级比```application.yml```低。并且其他属性也可以被添加成为创建一个Spring Boot 应用进程的一部分。

因为排序规则```bootstrap```属性优先级最高，但是注意并不是```bootstrap.yml```数据都是，有些优先级低但是被默认设置。

你可以继承上下文层次通过设置任何```ApplicationContext```你创建的父上下文、例如使用自己的接口或者使用```SpringApplicationBuilder```便捷方法(```parent()```，```child()```和```sibling()```)。启动上下文将最先最优先被加载，调用栈的每个上下文都有他们自己的启动属性来避免一些启动项无意的让父属性覆盖了子属性。每个上下文也有一个不同的```spring.application.name```和一个不同启动属性当有一个配置服务器。正常的spring 应用上下文举动遵循举动规则:子上下文覆盖父上下文的属性通过名字也可以通过属性资源名(如果子有一个属性资源的名称和父一样,那父不会覆盖子)。

注意```SpringApplicationBuilder```允许你共享一个 ```Environment```在整个调用栈里。但是这不是默认。因此，兄弟级上下文不需要拥有相同的配置文件或属性源，即使它们共有一些父属性。

#### 修改启动属性的位置

```bootstrap.yml```位置可以使用```spring.cloud.bootstrap.name```(默认叫bootstrap)或者```spring.cloud.bootstrap.location```(默认为空)。例如在系统属性中。这些属性都有类似的```spring.config.*```相同名字的属性。并且它们会被设置到启动```ApplicationContext```通过在它们的```Environment```设置。如果有一个激活的profile(来自```spring.profiles.active```或者通过```Environment```API建立的)那么这些属性也会被读取进去，就像传统的springboot应用一样。例如```bootstrap-development.properties```来自development profile.

#### 覆盖启动属性

一些资源加入你的应用通过上下文通常是"启动"（例如从一个配置服务器）并且默认他们不能在本地覆盖,除了命令行。如果你想要允许应用来如果启动属性通过自己的系统环境或者配置文件,那么启动属性必须授予允许通过设置```spring.cloud.config.allowOverride=true```(这个在本地不工作).一旦这个属性被设置那么有一些细粒度的设置来控制启动属性依赖系统属性的位置合应用本地配置:```spring.cloud.config.overrideNone=true```来覆盖一些本地属性,```spring.cloud.config.overrideSystemProperties=false```如果只是系统属性和环境变量可以被覆盖，但本地配置文件不可以。

#### 自定义启动配置

启动上下文可以被自定义来做你想要的任何事情通过添加键值对到```/META-INF/spring.factories```，key为```org.springframework.cloud.bootstrap.BootstrapConfiguration```。这是一个以逗号分隔的Spring```@Configuration```类列表(将会被创建进入这个context)。任何的bean你想要启用在主要的应用上下文中注入可以创建在这里。并且特别使用```@Bean```能够被```ApplicationContextInitializer```标注。如果你想要慢点注入，使用```@Order```
> 要特别小心在添加定制化```BootstrapConfiguration```你添加的类没有```@ComponentScanned```到你的主应用上下文是错误的。使用一个单独的包名，用于未被您```@ componentscan```或```@ springbootapplication```注释的配置类所覆盖的引导配置类。

#### 自定义启动属性资源

外部配置的默认属性资源被配置服务器的启动进程添加，但是你可以添加额外的资源通过添加```PropertySourceLocator```的bean到启动上下文(例如```spring.factories```)。你可以使用这个来插入额外的属性通过不同的服务器或者数据库实例。

例如,考虑以下简单的自定义定位器:

<pre>
public class CustomPropertySourceLocator implements PropertySourceLocator{
	@Override
	public PropertySource<?> locate(Environment environment) {
		return new MapPropertySource("customProperty", 
				Collections.singletonMap("property.from.sample.custom.source", "worked as intend"));
	}
}
</pre>

```Environment```传递的是```ApplicationContext```将被创建。也就是说，我们提供额外的属性来源。它将已有自己正常spring boot提供的属性所以你可以使用这些来定位一个资源属性到这个```Environment```中(例如通过使用key ```spring.application.name```,这个已经在默认配置服务器的时候定位生成了)

如果你创建一个jar里面有class，你添加一个```META-INF/spring.factories```包括:

<pre>
org.springframework.cloud.bootstrap.BootstrapConfituration=sample.custom.CustomPropertySourceLocator
</pre>

然后自定义属性```PropertySource```将会存在于任何在自己的classpath下包括这个jar的应用。

#### 环境改变

应用将会监听一个```EnvironmentChangeEvent```并且响应变化在一个标准方式组合(额外的```ApplicationListeners```用户可以添加一个```@Beans```在正常情况下)。当一个```EnvironmentChangeEvent```被监视它将会有一系列的键值被改变，应用将会使用这些到:

- 重新绑定任何```@ConfigurationProperties```bean在上下文

- 任何属性在```logging.level```设置日志等级

注意在```Environment```,配置客户端不为更改进行默认的轮询，并且通常我们不会建议使用方法来检测变化(监管你可能设置到一个```@Scheduled```注解)。如果你有一个伸缩性的客户端应用则广播```EnvironmentChangeEvent```到所有的实例比让他们轮训这个变化更好(例如使用```Spring Cloud Bus```)。

```EnvironmentChangeEvent```覆盖一个关于更新用例的大的类,和你通常改变一个```Environment```并且推送事件一样大(这些API都是共有并且是Spring的重要部分)。你可以验证改变绑定在```@ConfigurationProperties```bean通过访问```/configprops```端点(正常的Spring Boot执行器功能)。例如一个```DataSource```可以有它的```maxPoolSize```在运行时间改变(默认```DataSource```是一个```@ConfigurationProperties```bean 被Spring Boot创建)并且动态成长能力。重新绑定```@ConfigurationProperties```不需要覆盖其他另一类大型用例，您需要对refresh进行更多的控制，以及在整个ApplicationContext中需要更改为原子的地方。

#### 刷新作用域

一个Spring```@Bean```被标记位```@RefreshScope```并将特别的检查当有一个配置被改变。这个解决了一个问题关于仅在它们初始化阶段获取配置的有状态bean注入。例如,如果一个```DataSource```已经连接了当数据库的URL在```Environment```中改变。我们特别想要保持这些连写可以完成它们的工作。然后下一次有人从池中借用一个连接，他就会得到一个新的URL。

刷新作用域Bean是懒惰代理当它们被使用的时候才初始化(例如一个方法被调用)并且这个作用域作为一个初始值被缓存。为了让一个bean重新初始化在下一个方法只需要让这个缓存失效即可。

```RefreshScope```是一个上下文的bean并且有一个public方法```refreshAll()```来刷新所以作用域里的bean通过清除目标的缓存。通用有一个```refresh(String)```方法来刷新一个指定的bean通过名称。这个方法暴露在```/refresh```端点(通过HTTP或者JMX)

>注意:```@RefreshScope```工作（专门的）在一个```@Configuration```类，但是它可能引起一个意外的举动:例如它不意味着所有定义的```@Bean```都是```@RefreshScope```。特别，任何依赖这些bean不能依赖

#### 加密和解密

Spring Cloud有一个```Environment```预处理来本地加密属性值。关于配置服务器的相同规则，并且有一个相同的额外配置例如```encrypt.*```。因此你可以使用加密属性在表单```{cipher}*```并且相同的有一个验证的key并且将会在主应用上下文得到```Environment```之前解析它。为了在应用中使用加密功能你需要加入Spring Security RSA在你的classpath中。

如果你获取一个异常由于"非法的key大小"并且你使用sun的JDK。你需要安装JCE。

#### Endpoints

关于Spring Boot促进器应用有一些附加的管理endpoints:

- POST ```/env``` 来更新```Environment```并且重新绑定```@ConfigurationProperties```和log等级。

- ```/refresh```来重新加载启动上下文和刷新```@RefreshScope```bean。

- ```/restart```关闭```ApplicationContext```并且重启(默认不开启)

- ```/pause```和```/resume```来调用```Lifecycle```方法(```stop()```和```start()```在```ApplicationContext```)


### Spring Clound Commons:Common Abstractions

模式例如服务发现，负载均衡和断路器都有一个可以被任何Spring Cloud客户端实现的常用抽象层，独立于实现(例如发现通过Eureka或Consul)

#### @EnableDiscoveryClient

Commons提供```@EnableDiscoveryClient```注解。这将通过```META-INF/spring.factories```来寻找```DiscoveryClient```接口的实现类。发现客户端的实现类将添加一个配置类到```spring.factories```在```org.springframework.cloud.client.discovery.EnableDiscoveryClient```键。例如```DiscoveryClient```实现类:Spring Cloud Netflix Eureka,Spring Cloud Consul Discovery和Spring Cloud Zookeeper Discovery。

默认,```DiscoveryClient```的实现类将自动注册在本地Spring Boot服务器并且随着启动时发现。这个可以被禁止通过设置```autoRegister=false```在```@EnableDiscoveryClient```。

##### Health Indicator健康指标器

Commons创建一个Spring Boot```HealthIndicator```(```DiscoveryClient```实现类可以加入通过实现```DiscoveryHealthIndicator```)。为了禁用组件```HealthIndicator```可以设置```spring.cloud.discovery.client.composite-indicator.enabled=false```。一个类```HealthIndicator```基于```DiscoveryClient```自动配置(```DiscoveryClientHealthIndicator```)。为了关掉它 设置```spring.cloud.discovery.client.health-indicator.enabled=false```。关闭描述```DiscoveryClientHealthIndicator```字段可以设置```spring.cloud.discovery.client.health-indicator.include-description=false```，否则它可能成为```HealthIndicator```的```description```。

#### 服务注册

Commons提供一个```ServiceRegistry```接口提供像```register(Registration)```和```deregister(Registration)```允许自定义注册服务的方法。```Registration```是一个标记接口

<pre>
@Configuration
@EnableDiscoveryClient(autoRegister=false)
public class MyConfiguration {

	private ServiceRegistry registry;
	
	public MyConfiguration(ServiceRegistry registry) {
		this.registry = registry;
	}
	
	public void register() {
		Registration registration = constructRegistration();
		this.registry.register(registration);
	}

	private Registration constructRegistration() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
</pre>

每个```ServiceRegistry```实现类拥有自己的```Registry```实现类。

##### 服务注册自动注册

默认,```ServiceRegistry```实现类会自动注册到运行服务。为了停止这个举动。有两个方法。你可以设置```@EnableDiscoveryClient(autoRegister=false)```来永久禁用自动注册。也可以设置```spring.cloud.service-registry.auto-registration.enabled=false```来禁用这个举动通过配置。

##### 服务注册辅助endpoint

```/service-registry```辅助endpoint由Commons提供。这个endpoint依赖一个```Registration```bean在Spring Application Context。用GET调用```/service-registry/instance-status```放回```Registration```的状态。POST一个```String```body可以改变```Registration```到一个新值。

#### Spring RestTemplate作为一个辅助均衡客户端

```RestTemplate```可以使用ribbon自动配置。为了创建负载均衡的```RestTemplate```在```@Bean```中使用```@LoadBalanced```修饰。

> 一个```RestTemplate```bean不再是通过自动配置创建的。是通过个人应用创建的。

<pre>
@Configuration
public class MyConfiguration1 {

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}

class MyClass {
	@Autowired
	private RestTemplate restTemplate;
	
	private String doOtherStuff() {
		String reString = restTemplate.getForObject("http://stores/stores", String.class);
		return reString;
	}
}
</pre>

这个URI需要一个虚拟主机名(服务名,而不是主机名)。Ribbon客户端被用来创建一个完整的物理地址。

##### 入口错误请求

一个负载均衡```RestTemplate```可以被配置入口错误请求。默认这个逻辑是禁用的，可以开启通过添加Spring Retry到你的应用的classpath。负载均衡```RestTemplate```遵守一些与重新尝试失败请求相关的带配置值。如果你想要禁用这个入口可以设置```spring.cloud.loadbalancer.retry.enabled=false```。这个属性你可以用```client.ribbon.MaxAutoRetries```，```client.ribbon.MaxAutoRetriesNextServer```，和```client.ribbon.OkToRetryOnAllOperations```。具体查看Ribbon 文档。

>注意 ```client```在上面的例子，应该用你自己的Ribbon客户端名字来替代。

##### 多个RestTemplate对象

如果你想要多个```RestTemplate```没有负载均衡,创建一个```RestTemplate```bean并且像平常一样注入它。使用一个负载均衡```RestTemplate```使用```@LoadBalanced```标记当你创建你的```@Bean```

>重要：注意```@Primary```注解中的```RestTemplate```声明。用来消除不标记的```@Autowired```注解。

<pre>
@Configuration
public class MyConfiguration2 {
	
	@LoadBalanced
	@Bean
	RestTemplate loadBalanced() {
		return new RestTemplate();
	}
	
	@Primary
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

class MyClass1 {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @LoadBalanced
    private RestTemplate loadBalanced;

    public String doOtherStuff() {
        return loadBalanced.getForObject("http://stores/stores", String.class);
    }

    public String doStuff() {
        return restTemplate.getForObject("http://example.com", String.class);
    }
}
</pre>

> 提示:如果你看到错误像<pre>java.lang.IllegalArgumentException:Can not set 
org.springframework.web.client.RestTemplate field 
com.my.app.Foo.restTemplate to com.sun.proxy.$Proxy89
</pre>尝试注入```RestOperations```代替设置```spring.aop.proxyTargetClass=true```。

#### Spring WebFlux WebClient 作为一个负载均衡客户端

```WebClient```可以使用```LoadBalancerClient```被配置。一个```LoadBalancerExchangeFilterFunction```自动配置如果spring-webflux在classpath中

<pre>
@Configuration
public class MyConfiguration3 {
	
	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}

class MyClass3{
	@Autowired
	private LoadBalancerExchangeFilterFunction lbFunction;
	
	public Mono<String> doOtherStuff(){
		return WebClient.builder().baseUrl("http://stores")
	            .filter(lbFunction)
	            .build()
	            .get()
	            .uri("/stores")
	            .retrieve()
	            .bodyToMono(String.class);
	}
}
</pre>

这个URI需要使用一个虚拟主机名(是服务名，不是主机名)。```LoadBalancerClient```使用来创建一个完全的物理地址。

#### 忽略网络接口

有时候忽略某些命名网络接口是有用的。所以他们可以被加入通过服务发现注册(例如运行在一个Docker容器)。可以设置正则表达式列表，从而导致所需的网络接口被忽略。下面这个配置将会忽略"docker0"接口和所有的以"veth"开头的接口：

application.yml
<pre>
spring:
 cloud:
  inetutils:
   ignoredInterfaces:
    - docker0
    - veth.*
</pre>

也可以使用正则表达式列表强制使用指定的网络地址:

application.yml
<pre>
spring:
 cloud:
  inetutils:
   preferredNetworks:
    - 192.168
    - 10.0
</pre>

你可以忽视使用标志本地地址，查看``` Inet4Address.html.isSiteLocalAddress() ```

application.yml
<pre>
spring:
 cloud:
  inetutils:
   useOnlySiteLocalInterfaces: true
</pre>

#### HTTP Client 工厂

Spring Cloud Commons提供bean来创建相同Apache HTTP 客户端(```ApacheHttpClientFactory```)和OK HTTP 客户端(```OkHttpClientFactory```)。```OkHttpClientFacyory```只当OK HTTP jar在classpath中才创建。除此之外，Spring Cloud Commons提供bean来创建连接管理用于相同的客户端。```ApacheHttpClientConnectionManagerFactory```关于Apache HTTP客户端```OkHttpClientConnectionPoolFactory```关于 OK HTTP客户端的。你可以提供你自己的这些类的实现类如果你想要定制化一个HTTP客户端是如果在下游中创建的。你也可以禁用创建功能通过```spring.cloud.httpclientFactories.apache.enabled```或```spring.cloud.httpclientfactories.ok.enable```为```false```。

## Spring Cloud 配置

Spring Cloud 配置提供服务器和客户端支持额外的配置在分布式系统中。对于配置服务器，您有一个中心位置来管理所有环境中应用程序的外部属性。相同客户端内容和服务器身份映射到Spring```Environment```和```PropertySource```实现类。所以他们在Spring应用中合并非常好。可以使用在任何应用运行于任何语言中。一个应用通过发布通道从开发环境移到测试环境到生产环境你可以管理这些环境之间的配置并且可以某些应用程序拥有迁移时需要运行的所有内容。默认服务器存储后端实现使用git可以更容易支持配置环境的标签版本。并且变得可接受到用于管理内容的工具。这个很容易添加多选择的实现和弄进Spring配置。

### Quick Start

### Spring Cloud Config Server

服务器提供一个HTTP，资源基础API关于额外的配置(name-value对，或者YAML内容)。服务很容易被嵌入到一个Spring Boot应用使用```@EnableConfigServer```注解。所以这个App是一个配置服务器:
ConfigServer.java
<pre>
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
	
	public static void main(String[] args) {
		SpringApplication.run(ConfigServer.class, args);
	}
	
}
</pre>
像其他Boot应用一样默认是8080端口，可以换成8888在常量的方式。最简单的，设置一个默认的配置仓库，通过```spring.config.name=configserver```来启动(这有一个```configserver.yml```在Config Server jar)。另一个是使用自己的```application.properties```例如:

application.properties
<pre>
server.port: 8888
spring.cloud.config.server.git.uri: file://${user.home}/config-repo
</pre>
```${user.home}/config-repo```是一个git地址包含YAML和properties文件。


>警告：使用本地git仓库只能用于测试。使用一个服务器来配置你的配置仓库在生产环境中。(就是生产环境不要用本地 最好有个配置服务器)

>警告：初始化clone你的配置仓库可以更快有效如果你只是保持文本文件在里面。如果你开始存储二进制文件，特别大时。你会体验到阻塞在第一次请求的时候或者会内存溢出。 (就是git里面文件太大了 服务器受不鸟了)

#### 环境仓库 Environment Repository

你想要存储配置服务器的配置数据到哪儿呢？管理这个举动的策略是```EnvironmentRepository```。服务```Environment```对象。这个```Environment```是一个Spring```Environment```(包含```propertySources```主功能)的灰度拷贝。```Environment```资源通过三个变量参数化表示:

- ```{application}```映射"spring.application.name"在客户端;

- ```{profile}```映射"spring.profiles.active"在客户端(逗号分隔的list);

- ```{label}```一个服务器功能标签一个"版本化"的配置文件集合。

仓库实现通常的举动像spring boot应用从"spring.config.name"中读取配置文件(或者从```{application}```)参数和"spring.profiles.active"(同```{profile}```)参数。文件的优先规则和通常的Boot应用一样: active profile有一个默认优先权。如果有多个profile则最后一个生效(如添加entry到```Map```中一样)。

例如:一个客户端应用有下面的启动配置：

bootstrap.yml
<pre>
spring:
 application:
  name: foo
 profiles:
  active: dev,mysql
</pre>

(通常在一个Spring Boot应用下，这些属性可以被设置成环境变量或者命令行参数)

如果仓库是文件基础，服务器会创建一个```Environment```来自```application.yml```(所有客户端共享)和```foo.yml```(有优先权)。如果YAML文件里面有文件记录spring profile，这些将会获得一个高优先权(根据列举的顺序)。如果有特别的profile YAML(或者properties)文件在里面同样也会优先权比默认高。高优先权被翻译成一个在前面```Environment```中的```PropertySource```列表.(同样有相同的规则在标准的Spring boot 应用)

##### Git后端

默认实现```EnvironmentRepository```使用一个Git后端这个非常方便来管理升级和物理环境，并且可以查看改变记录。改变仓库的位置你可以在配置服务器中(例如在```application.yml```)使用"spring.cloud.config.server.git.uri"配置变量。如果你设置一个```file:```前缀它可以是一个本地的仓库所以你可以获取的更快并且更容易相比于服务器，但是假如这么做的话服务器操作指向本地的仓库而不是clone它(如果配置服务器从不对“远程”存储库进行更改，那么它是否暴露并不重要)。来伸缩配置服务器和让他更加高可用，你可能需要很多个服务器的实例来记录相同的仓库，所以一个共享文件系统需要。甚至这么做比使用```ssh:```协议来共享文件仓库更好。所以服务器可以clone它并使用一个本地工作拷贝当做缓存。

仓库实现映射成```{label}```HTTP资源参数对一个git label(commit id,branch name or tag).如果git分支或者名称包括一个斜线("/")那么在HTTP URL转化成一个特别的字符串"(_)"来替代。例如，标签```foo/bar```变成```foo(_)bar```小心使用斜线在URL中如果你使用命令行客户端例如curl。

##### Git URI占位符

Spring Cloud配置服务器支持一个使用占位符的git仓库URL为```{application}```和```{profile}```、```{label}```如果你需要。请记住标签是作为一个git标签应用的。所以你可以更容易支持"一个应用一个命名"使用规则 如:

<pre>
spring:
 cloud:
  config:
   server:
    git:
     uri: https://github.com/myorg/{application} //指应用名称
</pre>

或者一个"一个profile一个命名"使用规则但是匹配模式为```{profile}```。

##### 模式匹配和多仓库

当然支持更多更复杂的需求通过模式匹配应用和profile名称。模式格式化是一个```{application}/{profile}```名称的通配符逗号分隔的列表,如:

<pre>
spring:
 cloud:
  config:
   server:
    git:
     uri: https://github.com/spring-cloud-samples/config-repo
     repos:
      simple: https://github.com/simple/config-repo
      special:
       pattern: special*/dev*,*special*/dev*
       uri: https://github.com/special/config-repo
      local:
       pattern: local*
       uri: file:/home/configsvc/config-repo
</pre>

如果```{application}/{profile}```没有匹配任何，它将使用默认的uri定义"spring.cloud.config.server.git.uri".在上面的例子中,“simple”仓库,匹配```simple/*```(也就是说，它只匹配所有配置文件中名为“simple”的应用程序)。"local"在profile中匹配所有"local"开头。

```pattern```可以是一个数组(properties中是 ```[0]```,```[1]```)：
<pre>
spring:
 cloud:
  config:
   server:
    git:
     uri: https://github.com/spring-cloud-samples/config-repo
     repos:
      development:
       pattern:
        - '*/development'
        - '*/staging'
       uri: https://github.com/development/config-repo
      staging:
       pattern:
        - '*/pa'
        - '*/production'
       uri: https://github.com/staging/config-repo
</pre>

> 注意:Spring Cloud将会猜测一个包含不以```*```结尾的概要文件的模式，意味着您实际上想要匹配从这个模式开始的概要文件列表(so ```*/ staging```是```[" */staging "，" */staging，* "])```的快捷方式。这是一个常见的地方，你需要在“开发”配置文件中运行应用程序，例如远程的“Cloud”配置文件。

每个仓库也能随意存储配置文件在子目录中，并且pattern来寻找这些目录可以注明```searchPaths```，例如在高等级下:
<pre>
spring:
 cloud:
  config:
   server:
    git:
     uri: https://github.com/spring-cloud-samples/config-repo
     searchPaths: foo,bar* //目录名
</pre>

这个例子服务器寻找配置文件在高等级下并且在"foo/"子目录和任何的子目录名称以"bar"开头。

默认服务器clone远程仓库当配置第一次被请求时，服务器可以配置来clone仓库在启动时候,例如在最高登记下:
<pre>
spring:
 cloud:
  config:
   server:
    git:
     uri: http://git/common/config-repo.git
     repos:
      team-a:
       pattern: team-a-*
       cloneOnStart: true #就是这个 
       uri: http://git/team-a/config-repo.git
      team-b:
       pattern: team-b-*
       cloneOnStart: false
       uri: http://git/team-b/config-repo.git
      team-c:
       pattern: team-c*
       uri: http://git/team-a/config-repo.git
</pre>

在这个例子服务器在启动阶段克隆team-a的config-repo在接受任何请求之前，其他仓库并不会克隆知道仓库的配置被请求

> 注意： 设置一个仓库被clone当配置服务器开启时可以帮助快速鉴定一个微小配置资源(例如一个无效的URI仓库)，当配置服务器开启时候没有使用```cloneOnStart```来验证URI可靠性，那么不会报错知道第一次请求(说白了我擦就是让用cloneXXX这个参数来校验URI的可靠性)

##### 版本控制后端文件系统使用

> 警告:使用VCS基础后端文件(git,svn)被check out或者clone到本地系统。默认他们被扔到一个前缀是```config-repo-```的临时目录下。在linux,可能是```/tmp/config-repo-<randomid>```。一些系统可能会定时清空临时目录。这个可能会导致一些不寻常的举动例如找不到属性。为了避免这个问题，改变目录配置服务器使用,通过设置```spring.cloud.config.server.git.basedir```或者```spring.cloud.config.server.svn.basedir```到一个目录不是在系统临时数据。

##### 文件系统后端

当然有一个"native" profile在配置服务器上不适用Git,只是从本地classpath或者文件系统(一个静态URL"spring.cloud.config.server.native.searchLocations")读取配置文件。为了使用本地profile只需要开启配置服务器加上"spring.profiles.active=native"

>注意:记住使用```file:```文件资源前缀(默认使用classpath没有这个前缀)。像其他Spring Boot配置你可以使用```${}```-风格环境占位符，但是记住绝对路径在windows上需要一个额外的"/"如```file:///${user.home}/config-repo```


>警告:```searchLocations```默认是标志一个本地Spring Boot应用(如```[classpath:/,classpath:/confg,file:./,file:./config]```)。这个不会暴露在```application.properties```服务器到所有的客户端因为任何的属性资源存在于服务端都会在要发送给客户端之前被删除。


>提示:一个文件系统后端更加有用于快速建立和测试。如果使用它到生产环境你需要确定文件系统的可靠性，并且共享所有配置服务器的实例。

查找本地可以包含占位符```{application}```,```{profile}```和```{label}```。这种方式你可以隔离文件路径，并且选择一个你精通的策略(例如子文件每应用或者子目录一个profile)

如果你不在搜索路径使用占位符，仓库也可以添加```{label}```HTTP资源参数，所以资源文件被读取到任何的本地和一个子目录有着相同名字作为一个label(label属性有一个高优先级在Spring Environment中)。总是默认举动没有占位符相同添加一个搜索位置结尾为```/{label}/```.例如```file:/tmp/config```和```file:/tmp/config,file:/tmp/config/{label}```一样。这个举动可以被禁用通过设置```spring.cloud.config.server.native.addLabelLocations=false```。

##### Vault后端

Spring Cloud 配置服务器也支持Vault作为一个后端。

<pre>
Vault是一个工具来安全访问机密。任何东西都可以加密所以你可以加固它，比如一个API的key，密码，证书或者更多。Vault提供一个同意个接口来加密。提供一个加密访问控制和记录详情的审计日志。
</pre>

为了开启配置服务器使用一个Vault后端你只需运行你的配置服务器和一个```vault```的profile。例如你配置服务的```application.properties```你可以添加一个```spring.profiles.active=vault```。

默认配置服务器将会假定你的Vault服务器运行在```http://127.0.0.1:8200```。它也假定后端名称是```secret```并且key为```application```。这些默认是可以配置在你的服务器的```application.properties```。下面是一个配置Vault属性的表单。所有的属性前缀都是```spring.cloud.config.server.vault```.

<table  width=100%>
	<tr>
		<th width=50%>Name</th>
		<th width=50%>Default Value</th>
	</tr>
<tr>
	<td>host</td>
	<td>127.0.0.1</td>
</tr>
<tr>
	<td>port</td>
	<td>8200</td>
</tr>
<tr>
	<td>scheme</td>
	<td>http</td>
</tr>
<tr>
	<td>backend</td>
	<td>secret</td>
</tr>
<tr>
	<td>defaultKey</td>
	<td>application</td>
</tr>
<tr>
	<td>profileSeparator</td>
	<td>,</td>
</tr>
</table>


### Spring Cloud配置客户端

