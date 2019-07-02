package mxc.sdk.rabbit.service;

import mxc.sdk.rabbit.bean.BaseMqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by chenkaideng on 2019/1/22.
 */
public class AsyncMqSendService {

    @Autowired
    private final RabbitMqService rabbitMqService;

    public AsyncMqSendService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    /**
     * mq消息异步发送入口
     * @param mqMessage
     */
    @Async("taskExecutor")
    public void sendMessage(BaseMqMessage mqMessage) {
        rabbitMqService.sendMessage(mqMessage);
    }

    /**
     * mq消息异步发送入口
     * @param exchange
     * @param routingKey
     * @param message
     */
    @Async("taskExecutor")
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitMqService.sendMessage(exchange, routingKey, message);
    }
}
