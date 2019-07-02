package mxc.sdk.rabbit.service;

import mxc.sdk.rabbit.bean.BaseMqMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chenkaideng on 2019/1/22.
 */

public class MqSendService {

    @Autowired
    private final RabbitMqService rabbitMqService;

    public MqSendService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    /**
     * mq消息发送入口
     * @param mqMessage
     */
    public void sendMessage(BaseMqMessage mqMessage) {
        rabbitMqService.sendMessage(mqMessage);
    }

    /**
     * mq消息发送入口
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitMqService.sendMessage(exchange, routingKey, message);
    }
}
