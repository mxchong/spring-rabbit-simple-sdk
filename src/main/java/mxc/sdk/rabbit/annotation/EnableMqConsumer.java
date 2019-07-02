package mxc.sdk.rabbit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mxc.sdk.rabbit.config.MqConnectionConfig;
import mxc.sdk.rabbit.config.consumer.MqConsumerConfig;
import mxc.sdk.rabbit.config.consumer.MqListenerConfig;
import org.springframework.context.annotation.Import;

/**
 * Created by chenkaideng on 2019/1/21.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MqConnectionConfig.class, MqConsumerConfig.class, MqListenerConfig.class})
@Documented
public @interface EnableMqConsumer {
}
