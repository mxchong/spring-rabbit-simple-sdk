package mxc.sdk.rabbit.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by chenkaideng on 2019/1/24.
 *
 * 如果exchange不存在，connectionFactory会直接会报404，这种消息无法重拾回来了
 * 如果消息不能路由时(exchange确认不能路由到任何queue),并且设置了mandatory模式,会进行该callback
 */
public class RabbitMqReturnCallback implements RabbitTemplate.ReturnCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqReturnCallback.class);

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        LOGGER.error("--------------mq message can't be delivered to any queue--------------");
        LOGGER.error(String.format("message=[%s], exchange=[%s], routingKey=[%s]", message, s1, s2));
    }
}
