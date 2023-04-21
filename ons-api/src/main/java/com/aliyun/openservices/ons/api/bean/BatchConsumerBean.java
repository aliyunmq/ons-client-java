package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>{@code BatchConsumerBean} is used to integrate {@link BatchConsumer} with Spring Bean.</p>
 * <p>{@code BatchConsumerBean} 用于将 {@link BatchConsumer} 集成至 Spring Bean 中。</p>
 */
public class BatchConsumerBean implements BatchConsumer {

    /**
     * <p>This field needs to be injected and specifies the properties that construct the {@code BatchConsumer}
     * instance. For specific supported attributes, see {@link PropertyKeyConst}.</p>
     * <p>需要注入该字段，指定构造 {@code BatchConsumer} 实例的属性，具体支持的属性详见 {@link PropertyKeyConst}。</p>
     *
     * @see BatchConsumerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * <p>By injecting this field, subscription of topic is completed when starting {@code BatchConsumer}.</p>
     * <p>通过注入该字段，在启动 {@code BatchConsumer} 时完成 Topic 的订阅。</p>
     *
     * @see BatchConsumerBean#setSubscriptionTable(Map)
     */
    private Map<Subscription, BatchMessageListener> subscriptionTable;

    private BatchConsumer batchConsumer;

    @Override
    public boolean isStarted() {
        return this.batchConsumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.batchConsumer.isClosed();
    }

    /**
     * <p>Start this {@code BatchConsumer} instance, and configure it as the initialization method of the Bean.</p>
     * <p>启动该 {@code BatchConsumer} 实例，建议配置为 Bean 的 init-method。</p>
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        if (null == this.subscriptionTable) {
            throw new ONSClientException("subscriptionTable not set");
        }

        this.batchConsumer = ONSFactory.createBatchConsumer(this.properties);

        for (final Map.Entry<Subscription, BatchMessageListener> next : this.subscriptionTable.entrySet()) {
            this.subscribe(next.getKey().getTopic(), next.getKey().getExpression(), next.getValue());
        }

        this.batchConsumer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.batchConsumer != null) {
            this.batchConsumer.updateCredential(credentialProperties);
        }
    }

    /**
     * <p>Shutdown this {@code BatchConsumer} instance, and configure it as the destruction method of the Bean.</p>
     * <p>关闭该 {@code BatchConsumer} 实例，建议配置为 Bean 的 destroy-method。</p>
     */
    @Override
    public void shutdown() {
        if (this.batchConsumer != null) {
            this.batchConsumer.shutdown();
        }
    }

    @Override
    public void subscribe(final String topic, final String subExpression, final BatchMessageListener listener) {
        if (null == this.batchConsumer) {
            throw new ONSClientException("subscribe must be called after BatchConsumerBean started");
        }
        this.batchConsumer.subscribe(topic, subExpression, listener);
    }

    @Override
    public void unsubscribe(final String topic) {
        if (null == this.batchConsumer) {
            throw new ONSClientException("unsubscribe must be called after BatchConsumerBean started");
        }
        this.batchConsumer.unsubscribe(topic);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public Map<Subscription, BatchMessageListener> getSubscriptionTable() {
        return subscriptionTable;
    }

    public void setSubscriptionTable(final Map<Subscription, BatchMessageListener> subscriptionTable) {
        this.subscriptionTable = subscriptionTable;
    }
}