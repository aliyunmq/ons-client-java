package com.aliyun.openservices.ons.client.rocketmq.impl;

import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.client.rocketmq.PushConsumer;
import java.util.Properties;

public class OrderConsumerImpl extends PushConsumer implements OrderConsumer {

    public OrderConsumerImpl(final Properties properties) {
        super(properties);
    }

    @Override
    public void subscribe(String topic, String subExpression, MessageOrderListener listener) {

    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {

    }

    @Override
    public void subscribe(String topic, MessageSelector selector, MessageOrderListener listener) {

    }

    @Override
    public void rateLimit(String topic, double permitsPerSecond) {

    }
}
