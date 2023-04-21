package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import java.util.Properties;

/**
 * This {@code OrderProducerBean} is used to integrate {@link OrderProducer} into a Spring Bean.
 * <p>{@code OrderProducerBean} 用于将 {@link OrderProducer} 集成至 Spring Bean 中。
 */
public class OrderProducerBean implements OrderProducer {

    /**
     * This field needs to be injected to specify the properties for constructing the {@code OrderProducer}
     * instance. For details about the supported properties, please refer to {@link PropertyKeyConst}.
     * <p>需要注入该字段，指定构造{@code OrderProducer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see OrderProducerBean#setProperties(Properties)
     */
    private Properties properties;

    private OrderProducer orderProducer;

    /**
     * Starts this {@code OrderProducer} instance. It is recommended to configure it as the init-method of the Bean.
     * <p>启动该 {@code OrderProducer} 实例，建议配置为 Bean 的 init-method。
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        this.orderProducer = ONSFactory.createOrderProducer(this.properties);
        this.orderProducer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.orderProducer != null) {
            this.orderProducer.updateCredential(credentialProperties);
        }
    }

    /**
     * Shuts down this {@code OrderProducer} instance. It is recommended to configure it as the destroy-method of the
     * Bean.
     * <p>关闭该 {@code OrderProducer} 实例，建议配置为 Bean 的 destroy-method。
     */
    @Override
    public void shutdown() {
        if (this.orderProducer != null) {
            this.orderProducer.shutdown();
        }
    }

    @Override
    public boolean isStarted() {
        return this.orderProducer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.orderProducer.isClosed();
    }

    @Override
    public SendResult send(final Message message, final String shardingKey) {
        return this.orderProducer.send(message, shardingKey);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
}
