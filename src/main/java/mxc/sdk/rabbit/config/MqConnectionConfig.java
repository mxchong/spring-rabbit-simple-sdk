package mxc.sdk.rabbit.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Created by chenkaideng on 2019/1/21.
 */
public class MqConnectionConfig {

    @Autowired
    private Environment environment;

    /**
     * rabbit mq 连接配置
     * @return
     */
    @Bean
    public ConnectionFactory mqConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(environment.getRequiredProperty("rabbitmq.addresses"));
        connectionFactory.setUsername(environment.getRequiredProperty("rabbitmq.username"));
        connectionFactory.setPassword(environment.getRequiredProperty("rabbitmq.password"));
        connectionFactory.setVirtualHost(environment.getRequiredProperty("rabbitmq.vhost"));
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
}
