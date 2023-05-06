package com.aliyun.openservices.ons.client;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactoryAPI;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PullConsumer;
import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import org.apache.rocketmq.client.java.impl.consumer.OrderConsumerImpl;
import org.apache.rocketmq.client.java.impl.producer.ONSProducerImpl;
import org.apache.rocketmq.client.java.impl.consumer.PushConsumerImpl;
import java.util.Properties;

public class ONSFactoryImpl implements ONSFactoryAPI {
    @Override
    public Producer createProducer(Properties properties) {
        return new ONSProducerImpl(properties);
    }

    @Override
    public Consumer createConsumer(Properties properties) {
        return new PushConsumerImpl(properties);
    }

    @Override
    public BatchConsumer createBatchConsumer(Properties properties) {
        throw new UnsupportedOperationException("Batch consumer is not supported");
    }

    @Override
    public OrderProducer createOrderProducer(Properties properties) {
        return new ONSProducerImpl(properties);
    }

    @Override
    public OrderConsumer createOrderedConsumer(Properties properties) {
        return new OrderConsumerImpl(properties);
    }

    @Override
    public TransactionProducer createTransactionProducer(Properties properties, final LocalTransactionChecker checker) {
        return new ONSProducerImpl(properties, checker);
    }

    @Override
    public PullConsumer createPullConsumer(Properties properties) {
        return null;
    }
}

