package com.aliyun.openservices.ons.client;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactoryAPI;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PullConsumer;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import java.util.Properties;
import org.apache.rocketmq.client.java.impl.consumer.ONSOrderConsumerImpl;
import org.apache.rocketmq.client.java.impl.consumer.ONSPullConsumerImpl;
import org.apache.rocketmq.client.java.impl.consumer.ONSPushConsumerImpl;
import org.apache.rocketmq.client.java.impl.producer.ONSProducerImpl;

public class ONSFactoryImpl implements ONSFactoryAPI {
    @Override
    public Producer createProducer(Properties properties) {
        return new ONSProducerImpl(properties);
    }

    @Override
    public Consumer createConsumer(Properties properties) {
        return new ONSPushConsumerImpl(properties);
    }

    @Override
    public OrderProducer createOrderProducer(Properties properties) {
        return new ONSProducerImpl(properties);
    }

    @Override
    public OrderConsumer createOrderedConsumer(Properties properties) {
        return new ONSOrderConsumerImpl(properties);
    }

    @Override
    public TransactionProducer createTransactionProducer(Properties properties, final LocalTransactionChecker checker) {
        return new ONSProducerImpl(properties, checker);
    }

    @Override
    public PullConsumer createPullConsumer(Properties properties) {
        return new ONSPullConsumerImpl(properties);
    }
}

