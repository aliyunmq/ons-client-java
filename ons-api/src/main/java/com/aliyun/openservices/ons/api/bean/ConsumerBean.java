package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ExpressionType;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * The {@code ConsumerBean} class is used to integrate the {@link Consumer} into Spring Beans.
 * <p>{@code ConsumerBean}类用于将{@link Consumer}集成到Spring Bean中.</p>
 */
public class ConsumerBean implements Consumer {
    /**
     * This field needs to be injected, specifying the properties to construct a {@code Consumer} instance.
     * 具体支持的属性详见{@link PropertyKeyConst}
     * <p>需要注入该字段，指定构造{@code Consumer}实例的属性。</p>
     *
     * @see ConsumerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * By injecting this field, the Topic subscription is completed when starting the {@code Consumer}.
     * <p>通过注入该字段，在启动{@code Consumer}时完成Topic的订阅。</p>
     *
     * @see ConsumerBean#setSubscriptionTable(Map)
     */
    private Map<Subscription, MessageListener> subscriptionTable;

    private Consumer consumer;

    /**
     * Start the {@code Consumer} instance, it is recommended to configure this as the Bean's init-method.
     * <p>启动该{@code Consumer}实例，建议配置为Bean的init-method。</p>
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        if (null == this.subscriptionTable) {
            throw new ONSClientException("subscriptionTable not set");
        }

        this.consumer = ONSFactory.createConsumer(this.properties);

        for (Entry<Subscription, MessageListener> next : this.subscriptionTable.entrySet()) {
            if ("com.aliyun.openservices.ons.api.impl.notify.ConsumerImpl"
                .equals(this.consumer.getClass().getCanonicalName())
                && (next.getKey() instanceof SubscriptionExt)) {
                SubscriptionExt subscription = (SubscriptionExt) next.getKey();
                for (Method method : this.consumer.getClass().getMethods()) {
                    if ("subscribeNotify".equals(method.getName())) {
                        try {
                            method.invoke(consumer, subscription.getTopic(), subscription.getExpression(),
                                subscription.isPersistence(), next.getValue());
                        } catch (Exception e) {
                            throw new ONSClientException("subscribeNotify invoke exception", e);
                        }
                        break;
                    }
                }

            } else {
                Subscription subscription = next.getKey();
                if (subscription.getType() == null || ExpressionType.TAG.name().equals(subscription.getType())) {

                    this.subscribe(subscription.getTopic(), subscription.getExpression(), next.getValue());

                } else if (ExpressionType.SQL92.name().equals(subscription.getType())) {

                    this.subscribe(subscription.getTopic(), MessageSelector.bySql(subscription.getExpression()),
                        next.getValue());
                } else {

                    throw new ONSClientException(String.format("Expression type %s is unknown!",
                        subscription.getType()));
                }
            }

        }

        this.consumer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.consumer != null) {
            this.consumer.updateCredential(credentialProperties);
        }
    }

    /**
     * Shutdown the {@code Consumer} instance, it is recommended to configure this as the Bean's destroy-method.
     * <p>关闭该{@code Consumer}实例，建议配置为Bean的destroy-method。</p>
     */
    @Override
    public void shutdown() {
        if (this.consumer != null) {
            this.consumer.shutdown();
        }
    }

    @Override
    public void subscribe(String topic, String subExpression, MessageListener listener) {
        if (null == this.consumer) {
            throw new ONSClientException("subscribe must be called after consumerBean started");
        }
        this.consumer.subscribe(topic, subExpression, listener);
    }

    @Override
    public void subscribe(final String topic, final MessageSelector selector, final MessageListener listener) {
        if (null == this.consumer) {
            throw new ONSClientException("subscribe must be called after consumerBean started");
        }
        this.consumer.subscribe(topic, selector, listener);
    }

    @Override
    public void rateLimit(String topic, double permitsPerSecond) {
        this.consumer.rateLimit(topic, permitsPerSecond);
    }

    @Override
    public void unsubscribe(String topic) {
        if (null == this.consumer) {
            throw new ONSClientException("unsubscribe must be called after consumerBean started");
        }
        this.consumer.unsubscribe(topic);
    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {
        if (null == this.consumer) {
            throw new ONSClientException("setOffsetStore must be called after consumerBean started");
        }
        this.consumer.setOffsetStore(offsetStore);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Map<Subscription, MessageListener> getSubscriptionTable() {
        return subscriptionTable;
    }

    public void setSubscriptionTable(Map<Subscription, MessageListener> subscriptionTable) {
        this.subscriptionTable = subscriptionTable;
    }

    @Override
    public boolean isStarted() {
        return this.consumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.consumer.isClosed();
    }
}
