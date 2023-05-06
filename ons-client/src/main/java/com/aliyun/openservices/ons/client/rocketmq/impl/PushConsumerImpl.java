package com.aliyun.openservices.ons.client.rocketmq.impl;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.client.rocketmq.PushConsumer;
import java.util.Properties;

public class PushConsumerImpl extends PushConsumer implements Consumer {
    public PushConsumerImpl(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void updateCredential(Properties credentialProperties) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void subscribe(String topic, String subExpression, MessageListener listener) {

    }

    @Override
    public void subscribe(String topic, MessageSelector selector, MessageListener listener) {

    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {

    }

    @Override
    public void rateLimit(String topic, double permitsPerSecond) {

    }

    @Override
    public void unsubscribe(String topic) {

    }
}
