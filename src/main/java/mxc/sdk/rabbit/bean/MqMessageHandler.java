package mxc.sdk.rabbit.bean;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * Created by chenkaideng on 2019/1/21.
 */
public abstract class MqMessageHandler<T> {

    //是否开启消费者并发自动扩展，默认关闭
    protected Boolean enableConsumerAutoExpand = Boolean.TRUE;
    //初始并发消费者数量，默认1
    protected Integer concurrentConsumers = 1;
    //最大并发消费者数量，默认10
    protected Integer maxConcurrentConsumers = 10;
    //启动新消费者之间的时间间隔，默认2秒
    protected Integer startConsumerMinInterval = 2000;

    /**
     * 消费者的队列绑定相关配置
     * @param bindingObject
     */
    public abstract void initBinding(BindingObject bindingObject);


    /**
     * 消息处理
     * @param message
     */
    public abstract void handleMessage(T message);


    public void adjustContainer(SimpleMessageListenerContainer messageContainer) {
        //如果开启了消费者并发扩展
        if (enableConsumerAutoExpand) {
            messageContainer.setConcurrentConsumers(concurrentConsumers);
            messageContainer.setMaxConcurrentConsumers(maxConcurrentConsumers);
            messageContainer.setStartConsumerMinInterval(startConsumerMinInterval);
        }
    }

    /**
     * 是否自动调用initBinding方法，默认返回true.
     * @return 默认返回true
     */
    public boolean isAutoBinding() {
        return true;
    }
}
