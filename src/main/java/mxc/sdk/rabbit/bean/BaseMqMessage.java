package mxc.sdk.rabbit.bean;

import java.io.Serializable;

/**
 * Created by chenkaideng on 2019/1/22.
 */
public class BaseMqMessage implements Serializable {

    //路由
    private String exchange;

    //消息路由key
    private String routingKey;

    //消息时间类型
    private String eventType;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
