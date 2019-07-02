package mxc.sdk.rabbit.config.consumer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import mxc.sdk.rabbit.annotation.MqListener;
import mxc.sdk.rabbit.bean.BindingObject;
import mxc.sdk.rabbit.converter.MqMessageConverter;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Created by chenkaideng on 2019/2/14.
 */
public class MqListenerAnnotationProcessor implements BeanPostProcessor {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private ConnectionFactory mqConnectionFactory;

    private final Boolean retryPolicySet = Boolean.FALSE;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                MqListener mqListener = (MqListener) AnnotationUtils.getAnnotation(method, MqListener.class);
                if (mqListener != null) {
                    processRabbitAdmin(mqListener, method, bean);
                }
            }
        });
        return bean;
    }

    /**
     * 这个方法的处理流程：
     * STEP1）先创建Exchange
     * STEP2) 在创建Queue
     * STEP3）绑定Queue与Exchange
     */
    protected void processRabbitAdmin(MqListener mqListener, Method method, Object bean) {
        //获取自定义的消费者配置
        BindingObject bindingObject = new BindingObject();
        bindingObject.addBinding(mqListener.queue(), mqListener.exchange(), mqListener.routingKey());

        SimpleMessageListenerContainer messageContainer = new SimpleMessageListenerContainer();
        messageContainer.setConnectionFactory(mqConnectionFactory);

        // 设置消息监听器
        setMessageListener(messageContainer, bean, method);

        // STEP1:
        createExchange(mqListener.exchange());

        // STEP2: declare Queue
        for (Queue queue : bindingObject.getQueues()) {
            //定义队列
            rabbitAdmin.declareQueue(queue);
            //将队列绑定到消息处理类
            messageContainer.addQueues(queue);
        }

        // SETP3: bing Queue To Exchange
        //绑定队列
        for (Binding binding : bindingObject.getBindings()) {
            rabbitAdmin.declareBinding(binding);
        }
        //配置消息接收失败重试策略
        if (retryPolicySet) {
            messageContainer.setAdviceChain(new Advice[]{MqConsumerAdvice.missingMessageIdAdvice(), MqConsumerAdvice.methodInterceptor()});
        }
        //设置消费者配置
        initConsumerConfig(messageContainer, mqListener);

        //启动消息处理
        if (messageContainer.getQueueNames().length > 0 && messageContainer.getMessageListener() != null) {
            messageContainer.start();
        }
    }

    /**
     * 告诉RabbitMq Server, 创建一个exchange
     * @param exchangeName
     */
    private void createExchange(String exchangeName) {
        TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
        rabbitAdmin.declareExchange(topicExchange);
    }

    /**
     * 设置消息监听器
     */
    protected void setMessageListener(SimpleMessageListenerContainer messageContainer, Object bean, Method method) {
        if (messageContainer == null || bean == null) {
            return;
        }
        if (method.getParameters().length != 1) {
            throw new RuntimeException("method parameter's num is only be one");
        }
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDefaultListenerMethod(method.getName());
        messageListenerAdapter.setDelegate(bean);
        Type actualType = method.getParameterTypes()[0];
        messageListenerAdapter.setMessageConverter(new MqMessageConverter((Class) actualType));
        messageContainer.setMessageListener(messageListenerAdapter);
    }

    /**
     * 初始化消费者线程配置
     */
    protected void initConsumerConfig(SimpleMessageListenerContainer messageContainer, MqListener mqListener) {
        if (mqListener.enableConsumerAutoExpand()) {
            messageContainer.setConcurrentConsumers(mqListener.concurrentConsumers());
            messageContainer.setMaxConcurrentConsumers(mqListener.maxConcurrentConsumers());
            messageContainer.setStartConsumerMinInterval(mqListener.startConsumerMinInterval());
        }
    }
}
