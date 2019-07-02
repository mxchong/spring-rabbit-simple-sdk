package mxc.sdk.rabbit.config.producer;

import java.lang.annotation.Annotation;

import mxc.sdk.rabbit.annotation.MqSender;
import mxc.sdk.rabbit.service.AsyncMqSendService;
import mxc.sdk.rabbit.service.MqSendService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chenkaideng on 2019/2/12.
 */
@Aspect
public class MqSenderAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqSenderAspect.class);

    @Autowired
    private AsyncMqSendService asyncMqSendService;

    @Autowired
    private MqSendService mqSendService;

    @Pointcut("@annotation(mxc.sdk.rabbit.annotation.MqSender)")
    public void mqSenderCut() {
    }

    @AfterReturning(pointcut = "mqSenderCut()", returning = "object")
    public void afterReturning(JoinPoint point, Object object) throws Throwable {
        if (null == object) {
            LOGGER.debug(String.format("error, return message is null"));
            return;
        }

        Annotation[] annotations = ((MethodSignature) point.getStaticPart().getSignature()).getMethod().getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(MqSender.class)) {
                sendMessage((MqSender) annotation, object);
            }
        }
    }

    @AfterThrowing(pointcut = "mqSenderCut()", throwing = "exception")
    public void afterThrowing(Exception exception) throws Throwable {
        LOGGER.error(String.format("mqSenderCut() is throw a exception:%s", exception.toString()));
        throw exception;
    }

    private void sendMessage(MqSender mqSender, Object object) {
        if (mqSender.isAsync()) {
            asyncMqSendService.sendMessage(mqSender.exchange(), mqSender.routingKey(), object);
        } else {
            mqSendService.sendMessage(mqSender.exchange(), mqSender.routingKey(), object);
        }
    }
}