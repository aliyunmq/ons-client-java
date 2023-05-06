package com.aliyun.openservices.ons.client.rocketmq.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.client.ClientAbstract;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class ProducerImpl extends ClientAbstract implements Producer, OrderProducer, TransactionProducer {

    public ProducerImpl(final Properties properties) {
        super(properties);
    }

    public ProducerImpl(final Properties properties, final LocalTransactionChecker localChecker) {
        this(properties);
        // TODO
    }

    @Override
    public SendResult send(Message message) {
        return null;
    }

    @Override
    public void sendOneway(Message message) {

    }

    @Override
    public void sendAsync(Message message, SendCallback sendCallback) {

    }

    @Override
    public void setCallbackExecutor(ExecutorService callbackExecutor) {

    }

    @Override
    public SendResult send(Message message, String shardingKey) {
        return null;
    }

    @Override
    public SendResult send(Message message, LocalTransactionExecuter executer, Object arg) {
        return null;
    }
}
