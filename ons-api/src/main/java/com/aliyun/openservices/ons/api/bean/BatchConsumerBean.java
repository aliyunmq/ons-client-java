package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.util.Map;
import java.util.Properties;

/**
 * {@code BatchConsumerBean}用于将{@link BatchConsumer}集成至Spring Bean中
 */
public class BatchConsumerBean implements BatchConsumer {
    /**
     * 需要注入该字段，指定构造{@code BatchConsumer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see BatchConsumerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * 通过注入该字段，在启动{@code BatchConsumer}时完成Topic的订阅
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
     * 启动该{@code BatchConsumer}实例，建议配置为Bean的init-method
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
     * 关闭该{@code BatchConsumer}实例，建议配置为Bean的destroy-method
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

    public void setSubscriptionTable(
            final Map<Subscription, BatchMessageListener> subscriptionTable) {
        this.subscriptionTable = subscriptionTable;
    }
}
