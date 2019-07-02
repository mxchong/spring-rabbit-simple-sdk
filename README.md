# spring-rabbit-simple-sdk
在spring4.0x的项目背景下，封装的一个好用简便的rabbitmq相关的sdk.

## 封装内容
### 消息base：
BaseMqMessage消息基类，所有的业务消息都需要继承它，其中的eventType(根据业务需求是否要填)和routingKey（根据MqSendService调用的方法，灵活的看把key写在哪里）都为非必填

### 生产者：
`@EnableMqProducer` 项目使用rabbitmq的生产者开关注解，初始化Mq基础连接配置和RabbitTemplate实例</br>
`@MqProducer` 生产者注解，用来生产发送消息

### 消费者：
`@EnableMqConsumer` 项目使用rabbitmq的消费者开关注解，初始化Mq基础连接配置和RabbitAdmin实例</br>
`@MqListener` 消费者注解，用来监听消费消息

### 异常：
发送消息失败回调RabbitMqReturnCallback，如果exchange不存在，connectionFactory会直接会报404，这种消息无法重拾回来了；
如果消息不能路由时(exchange确认不能路由到任何queue),并且设置了mandatory模式,会进行该callback，并且能够将消息继续处理，目前的处理先进行error日志打印

## 使用方式
### 生产者（发送消息）
在项目的启动类AppConfig头部增加注解`@EnableMqProducer`

|  方式 | 具体使用方式 |
| :- | :- |
| 注入 | ```@Autowired MqSendService mqSendService;```</br>```mqSendService.sendMessage(#exchange, #routingKey, #message)```|
| 注解（建议这种方式） | 写一个普通类，然后用`@Component`实例化到spring容器中</br>```@MqSender(exchange=#exchange, routingKey=#routingKey)```</br> public T sendMessage(......;return message) |

### 消费者（处理消息）
在项目的启动类AppConfig头部增加注解`@EnableMqConsumer`

|  方式 | 具体使用方式 |
| :- | :- |
| 注入 | 写一个类必须是继承MqMessageHandler，然后用`@Component`实例化到spring容器中</br>继承MqMessageHandler必须重写initBinding(BindingObject bindingObject)和handleMessage(T message) |
| 注解（建议这种方式） | 写一个普通类用`@Component`实例化到spring容器中</br>在该类中任意写一个方法，该方法只有一个参数即为消息实体</br>在这个方法使用注解`@MqListener`，补充监听相关参数</br>方法体中，即可实现消费者的对于接收到消息进行下一步处理的业务逻辑 |
