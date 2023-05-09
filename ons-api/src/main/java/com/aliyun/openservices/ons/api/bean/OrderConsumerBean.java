
package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import java.util.Map;
import java.util.Properties;

/**
 * {@code OrderConsumerBean} is used to integrate {@link OrderConsumer} into Spring Bean.
 * <p>{@code OrderConsumerBean}用于将{@link OrderConsumer}集成至Spring Bean中。
 */
public class OrderConsumerBean implements OrderConsumer {
    /**
     * Inject this field to specify the properties for constructing the {@code OrderConsumer} instance.
     * See {@link PropertyKeyConst} for the supported properties.
     * <p>需要注入该字段，指定构造{@code OrderConsumer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see OrderConsumerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * Inject this field to subscribe to the Topic when starting the {@code OrderConsumer}.
     * <p>通过注入该字段，在启动{@code OrderConsumer}时完成Topic的订阅
     *
     * @see OrderConsumerBean#setSubscriptionTable(Map)
     */
    private Map<Subscription, MessageOrderListener> subscriptionTable;

    private OrderConsumer orderConsumer;

    @Override
    public boolean isStarted() {
        return this.orderConsumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.orderConsumer.isClosed();
    }

    /**
     * Start the {@code OrderConsumer} instance. It is recommended to configure it as the Bean's init-method.
     * <p>启动该{@code OrderConsumer}实例，建议配置为Bean的init-method
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        if (null == this.subscriptionTable) {
            throw new ONSClientException("subscriptionTable not set");
        }

        this.orderConsumer = ONSFactory.createOrderedConsumer(this.properties);

        for (final Map.Entry<Subscription, MessageOrderListener> next : this.subscriptionTable.entrySet()) {
            this.subscribe(next.getKey().getTopic(), next.getKey().getExpression(), next.getValue());
        }

        this.orderConsumer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.orderConsumer != null) {
            this.orderConsumer.updateCredential(credentialProperties);
        }
    }

    /**
     * Close the {@code OrderConsumer} instance. It is recommended to configure it as the Bean's destroy-method.
     * <p>关闭该{@code OrderConsumer}实例，建议配置为Bean的destroy-method
     */
    @Override
    public void shutdown() {
        if (this.orderConsumer != null) {
            this.orderConsumer.shutdown();
        }
    }

    @Override
    public void subscribe(final String topic, final String subExpression, final MessageOrderListener listener) {
        if (null == this.orderConsumer) {
            throw new ONSClientException("subscribe must be called after OrderConsumerBean started");
        }
        this.orderConsumer.subscribe(topic, subExpression, listener);
    }

    @Override
    public void subscribe(String topic, MessageSelector selector, MessageOrderListener listener) {
        if (null == this.orderConsumer) {
            throw new ONSClientException("subscribe must be called after OrderConsumerBean started");
        }
        this.orderConsumer.subscribe(topic, selector, listener);
    }

    public void setOffsetStore(final OffsetStore offsetStore) {
        if (null == this.orderConsumer) {
            throw new ONSClientException("SetOffsetStore must be called after orderConsumerBean started");
        }
        this.orderConsumer.setOffsetStore(offsetStore);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public Map<Subscription, MessageOrderListener> getSubscriptionTable() {
        return subscriptionTable;
    }

    public void setSubscriptionTable(
        final Map<Subscription, MessageOrderListener> subscriptionTable) {
        this.subscriptionTable = subscriptionTable;
    }
}