package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import java.util.Properties;

public class ONSOrderConsumerImpl extends ONSPushConsumer implements OrderConsumer {

    public ONSOrderConsumerImpl(final Properties properties) {
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
