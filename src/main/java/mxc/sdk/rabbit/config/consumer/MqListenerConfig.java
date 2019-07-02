package mxc.sdk.rabbit.config.consumer;

import org.springframework.context.annotation.Bean;

/**
 * Created by chenkaideng on 2019/2/21.
 */
public class MqListenerConfig {

    @Bean
    public MqListenerAnnotationProcessor mqListenerAnnotationProcessor() {
        return new MqListenerAnnotationProcessor();
    }
}
