package mxc.sdk.rabbit.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

/**
 * Created by chenkaideng on 2019/1/21.
 */
public class MqMessageConverter extends SimpleMessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqMessageConverter.class);
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    private Class actualType;

    public MqMessageConverter() {
    }

    public MqMessageConverter(Class actualType) {
        this.actualType = actualType;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            if (actualType.equals(String.class)) {
                return new String(message.getBody(), "utf-8");
            }
            return OBJECTMAPPER.readValue(message.getBody(), actualType);
        } catch (Exception e) {
            LOGGER.error("Could not read JSON: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected Message createMessage(Object object, MessageProperties messageProperties) {
        try {
            return super.createMessage(OBJECTMAPPER.writeValueAsString(object), messageProperties);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not write JSON: " + e.getMessage(), e);
        }
        return null;
    }
}
