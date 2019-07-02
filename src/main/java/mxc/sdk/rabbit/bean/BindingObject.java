package mxc.sdk.rabbit.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.util.StringUtils;

/**
 * Created by chenkaideng on 2019/1/21.
 */
public class BindingObject {

    public static final String DEFAULT_ROUNTINT_KEY = "#";

    private String defaultRountintKey = DEFAULT_ROUNTINT_KEY;

    //队列列表
    private List<Queue> queues = new ArrayList<Queue>();
    //绑定列表
    private List<Binding> bindings = new ArrayList<Binding>();

    /**
     * NoArgsConstructor
     */
    public BindingObject() {
    }

    /**
     * AllArgsConstructor
     * @param defaultRountintKey
     * @param queues
     * @param bindings
     */
    public BindingObject(String defaultRountintKey, List<Queue> queues, List<Binding> bindings) {
        this.defaultRountintKey = defaultRountintKey;
        this.queues = queues;
        this.bindings = bindings;
    }

    public String getDefaultRountintKey() {
        return defaultRountintKey;
    }

    public void setDefaultRountintKey(String defaultRountintKey) {
        this.defaultRountintKey = defaultRountintKey;
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public void setBindings(List<Binding> bindings) {
        this.bindings = bindings;
    }

    public void addBinding(String queueName, String exchange) {
        this.addBinding(queueName, exchange, null);
    }

    public void addBinding(String queueName, String exchange, String routingKey) {
        this.addBinding(queueName, exchange, routingKey, null);
    }

    public void addBinding(String queueName, String exchange, String routingKey, Map<String, Object> arguments) {
        if (StringUtils.isEmpty(queueName)) {
            throw new RuntimeException("invalid queueName:" + queueName);
        }

        if (StringUtils.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange:" + exchange);
        }

        String realRoutingKey = routingKey;
        if (StringUtils.isEmpty(realRoutingKey)) {
            realRoutingKey = this.defaultRountintKey;
        }
        this.addQueue(queueName);
        this.addBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchange, realRoutingKey, arguments));
    }

    private void addBinding(Binding binding) {
        bindings.add(binding);
    }

    private void addQueue(String queueName) {
        queues.add(new Queue(queueName));
    }
}
