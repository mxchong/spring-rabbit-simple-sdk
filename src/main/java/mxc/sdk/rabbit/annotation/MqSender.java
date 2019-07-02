package mxc.sdk.rabbit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenkaideng on 2019/2/12.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MqSender {
    /**
     * 交换机
     * @return
     */
    String exchange();

    /**
     * 路由key
     * @return
     */
    String routingKey() default "#";

    /**
     * 是否异步发送
     * @return
     */
    boolean isAsync() default false;
}
