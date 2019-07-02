package mxc.sdk.rabbit.config.consumer;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.retry.MissingMessageIdAdvice;
import org.springframework.retry.policy.MapRetryContextCache;

/**
 * Created by chenkaideng on 2019/2/15.
 */
public class MqConsumerAdvice {

    /**
     * 避免由于客户端没有带messageId导致receiver出现问题
     * Advice that can be placed in the listener delegate's advice chain to enhance the message with an ID if not present.
     * If an exception is caught on a redelivered message, rethrows it as an AmqpRejectAndDontRequeueException which signals the container to NOT requeue the message (otherwise we'd have infinite immediate retries).
     * If so configured, the broker can send the message to a DLE/DLQ. Must be placed before the retry interceptor in the advice chain.
     * @return
     */
    public static MissingMessageIdAdvice missingMessageIdAdvice() {
        return new MissingMessageIdAdvice(new MapRetryContextCache());
    }

    /**
     * 接收失败重试
     * @return
     */
    public static MethodInterceptor methodInterceptor() {
        //使用stateless（如果使用stateful，会因为重投递的消息体的redelivered标记而在第二次重投递时被MissingMessageIdAdvice抛出异常）
        //初始1秒，最大值限制为10分钟（防止因脏数据导致阻塞过久），每次重试会按指数增长（考虑发布时重启服务器的场景）
        //采用重试10分钟还失败，就抛弃的策略
        return  RetryInterceptorBuilder.stateless()
            .backOffOptions(1000L, 2D, 600000L)
            .maxAttempts(10)
            .build();
    }
}
