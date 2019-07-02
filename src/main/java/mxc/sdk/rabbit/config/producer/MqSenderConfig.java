package mxc.sdk.rabbit.config.producer;

import org.springframework.context.annotation.Bean;

/**
 * Created by chenkaideng on 2019/2/21.
 */
public class MqSenderConfig {

    @Bean
    public MqSenderAspect mqSenderAspect() {
        return new MqSenderAspect();
    }
}
