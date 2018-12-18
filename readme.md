### 使用netty实现高并发websocket服务


#### 1.使用maven打成jar包，在项目中引入

```xml
<dependency>
    <groupId>cn.x5456</groupId>
    <artifactId>netty-websocket</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 2.添加spring注解扫描的包
![](https://ws1.sinaimg.cn/large/006tNbRwly1fyatuwalerj31eu0i4jwg.jpg)

#### 3.继承抽象类WebSocketFrameHandler，加入spring容器中

```java
@Component
@Scope(scopeName = "prototype") // 一定要是多例的
public class WebSocketFrameHandlerImpl extends WebSocketFrameHandler {
}
```

#### 4.可选配置

![](https://ws4.sinaimg.cn/large/006tNbRwly1fyatz6t1v4j30ns07gdhm.jpg)