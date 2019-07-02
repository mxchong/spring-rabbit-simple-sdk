package mxc.sdk.rabbit.config.consumer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import mxc.sdk.rabbit.bean.BindingObject;
import mxc.sdk.rabbit.bean.MqMessageHandler;
import mxc.sdk.rabbit.converter.MqMessageConverter;
import org.aopalliance.aop.Advice;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * Created by chenkaideng on 2019/1/21.
 */
public class MqConsumerConfig {

    @Autowired(required = false)
    private List<MqMessageHandler> mqMessageHandlers;

    private final Boolean retryPolicySet = Boolean.TRUE;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(false);

        if (CollectionUtils.isNotEmpty(mqMessageHandlers)) {
            for (MqMessageHandler mqMessageHandler : mqMessageHandlers) {
                if (!mqMessageHandler.isAutoBinding()) {
                    continue;
                }
                //获取自定义的消费者配置
                BindingObject bindingObject = new BindingObject();
                mqMessageHandler.initBinding(bindingObject);
                SimpleMessageListenerContainer messageContainer = new SimpleMessageListenerContainer();
                messageContainer.setConnectionFactory(connectionFactory);
                //开始配置MQ消费者
                if (CollectionUtils.isEmpty(bindingObject.getQueues())) {
                    throw new RuntimeException("queues may not be empty");
                }
                if (CollectionUtils.isEmpty(bindingObject.getBindings())) {
                    throw new RuntimeException("bindings may not be empty");
                }
                // 设置消息监听器
                setMessageListener(messageContainer, mqMessageHandler);

                for (Queue queue : bindingObject.getQueues()) {
                    //定义队列
                    rabbitAdmin.declareQueue(queue);
                    //将队列绑定到消息处理类
                    messageContainer.addQueues(queue);
                }
                //绑定队列
                for (Binding binding : bindingObject.getBindings()) {
                    rabbitAdmin.declareBinding(binding);
                }
                //配置消息接收失败重试策略
                if (retryPolicySet) {
                    messageContainer.setAdviceChain(new Advice[]{MqConsumerAdvice.missingMessageIdAdvice(), MqConsumerAdvice.methodInterceptor()});
                }
                //设置自定义参数
                mqMessageHandler.adjustContainer(messageContainer);
                //启动消息处理
                if (messageContainer.getQueueNames().length > 0 && messageContainer.getMessageListener() != null) {
                    messageContainer.start();
                }

            }
        }
        return rabbitAdmin;
    }

    /**
     * 设置消息监听器
     *
     * @param messageContainer
     * @param mqMessageHandler
     */
    protected void setMessageListener(SimpleMessageListenerContainer messageContainer, MqMessageHandler mqMessageHandler) {
        if (messageContainer == null || mqMessageHandler == null) {
            return;
        }
        //判断泛型类型
        Type typeClass = mqMessageHandler.getClass().getGenericSuperclass();
        if (typeClass instanceof ParameterizedType) {
            Type actualType = ((ParameterizedType) typeClass).getActualTypeArguments()[0];
            messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new MqMessageConverter((Class) actualType)));
            return;
        }
        messageContainer.setMessageListener(new MessageListenerAdapter(mqMessageHandler, new SimpleMessageConverter()));
    }
}
