package mxc.sdk.rabbit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mxc.sdk.rabbit.bean.BaseMqMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chenkaideng on 2019/1/22.
 */
public class RabbitMqService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqService.class);
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public void sendMessage(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage is null");
        }
        try {
            rabbitTemplate.convertAndSend(getExchange(baseMqMessage), getRountingKey(baseMqMessage), baseMqMessage);
            LOGGER.info(String.format("send a mq message [%s]", OBJECTMAPPER.writeValueAsString(baseMqMessage)));
        } catch (AmqpException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        if (message == null) {
            throw new RuntimeException("invalid mqMessage is null");
        }
        if (StringUtils.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange is empty");
        }
        if (StringUtils.isEmpty(routingKey)) {
            throw new RuntimeException("invalid routingKey is empty");
        }
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            LOGGER.info(String.format("send a mq message exchange[%s] routingKey[%s] content[%s]，", exchange, routingKey, OBJECTMAPPER.writeValueAsString(message)));
        } catch (AmqpException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    private String getRountingKey(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage, mqMessage is null");
        }
        String routingKey = baseMqMessage.getRoutingKey();
        if (StringUtils.isEmpty(routingKey)) {
            throw new RuntimeException("invalid routingKey, routingKey is empty");
        }
        return routingKey;
    }

    private String getExchange(BaseMqMessage baseMqMessage) {
        if (baseMqMessage == null) {
            throw new RuntimeException("invalid mqMessage, mqMessage is null");
        }
        String exchange = baseMqMessage.getExchange();
        if (StringUtils.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange, exchange is empty");
        }
        return exchange;
    }
}
