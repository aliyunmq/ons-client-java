package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.client.ClientAbstract;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ONSPushConsumer extends ClientAbstract {
    private static final Logger log = LoggerFactory.getLogger(ONSPushConsumer.class);

    private static final int MAX_CACHED_MESSAGES_QUANTITY = 50000;
    private static final int MIN_CACHED_MESSAGES_QUANTITY = 100;
    private static final int DEFAULT_CACHED_MESSAGES_QUANTITY = 5000;

    private static final int MIN_CACHED_MESSAGE_MEMORY_IN_MIB = 16;
    private static final int MAX_CACHED_MESSAGE_MEMORY_IN_MIB = 2048;
    private static final int DEFAULT_CACHED_MESSAGE_MEMORY_IN_MIB = 512;

    private static final int DEFAULT_CONSUMPTION_THREADS_AMOUNT = 20;
    private static final int CONSUMPTION_THREADS_MAX_AMOUNT = 1000;
    protected final PushConsumerImpl pushConsumer;
    protected final PullConsumerImpl pullConsumer;

    protected final MessageModel messageModel;
    protected OffsetStore offsetStore = null;

    public ONSPushConsumer(Properties properties) {
        super(properties);
        final String consumerGroup = properties.getProperty(PropertyKeyConst.GROUP_ID);
        if (StringUtils.isBlank(consumerGroup)) {
            throw new ONSClientException("Group id is blank, please set it.");
        }
        // max cached message quantity.
        final String maxCachedMessagesQuantityProp = properties.getProperty(PropertyKeyConst.MaxCachedMessageAmount);
        int maxCachedMessagesQuantity = DEFAULT_CACHED_MESSAGES_QUANTITY;
        if (StringUtils.isNoneBlank(maxCachedMessagesQuantityProp)) {
            maxCachedMessagesQuantity = Integer.parseInt(maxCachedMessagesQuantityProp);
            maxCachedMessagesQuantity = Math.max(MIN_CACHED_MESSAGES_QUANTITY, maxCachedMessagesQuantity);
            maxCachedMessagesQuantity = Math.min(MAX_CACHED_MESSAGES_QUANTITY, maxCachedMessagesQuantity);
        }
        // max cached message bytes.
        final String maxCachedMessageSizeInMibProp = properties.getProperty(PropertyKeyConst.MaxCachedMessageSizeInMiB);
        int maxCachedMessageSizeInMib = DEFAULT_CACHED_MESSAGE_MEMORY_IN_MIB;
        if (StringUtils.isNoneBlank(maxCachedMessageSizeInMibProp)) {
            maxCachedMessageSizeInMib = Integer.parseInt(maxCachedMessageSizeInMibProp);
            maxCachedMessageSizeInMib = Math.max(MIN_CACHED_MESSAGE_MEMORY_IN_MIB, maxCachedMessageSizeInMib);
            maxCachedMessageSizeInMib = Math.min(MAX_CACHED_MESSAGE_MEMORY_IN_MIB, maxCachedMessageSizeInMib);
        }
        // consumption threads amount.
        int consumptionThreadsAmount = DEFAULT_CONSUMPTION_THREADS_AMOUNT;
        final String consumptionThreadsAmountProp = properties.getProperty(PropertyKeyConst.ConsumeThreadNums);
        if (StringUtils.isNoneBlank(consumptionThreadsAmountProp)) {
            consumptionThreadsAmount = Integer.parseInt(consumptionThreadsAmountProp);
        }
        if (consumptionThreadsAmount < 1 || consumptionThreadsAmount > CONSUMPTION_THREADS_MAX_AMOUNT) {
            throw new ONSClientException("Consumption thread amount is out of range [1, 1000]");
        }
        final String messageModelProp = properties.getProperty(PropertyKeyConst.MessageModel,
            PropertyValueConst.DEFAULT_MESSAGE_MODEL);
        this.messageModel = MessageModel.valueOf(messageModelProp);

        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            // message listener here will be overwritten.
            MessageListener messageListener = messageView -> ConsumeResult.SUCCESS;
            this.pushConsumer = new PushConsumerImpl(clientConfiguration, consumerGroup, new HashMap<>(), messageListener,
                maxCachedMessagesQuantity, maxCachedMessageSizeInMib * 1024 * 1024,
                consumptionThreadsAmount);
            this.pullConsumer = null;
        } else {
            this.pushConsumer = null;
            this.pullConsumer = new PullConsumerImpl(clientConfiguration, consumerGroup, false,
                Duration.ofSeconds(5), maxCachedMessagesQuantity, Integer.MAX_VALUE,
                maxCachedMessageSizeInMib * 1024 * 1024, Integer.MAX_VALUE);
        }
    }

    @Override
    public void start() {
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            final ClientId clientId = pushConsumer.getClientId();
            try {
                if (this.started.compareAndSet(false, true)) {
                    log.info("Begin to start the ONS push consumer, clientId={}", clientId);
                    this.pushConsumer.startAsync().awaitRunning();
                    log.info("ONS push consumer starts successfully, clientId={}", clientId);
                    return;
                }
                log.warn("ONS push consumer has been started before, clientId={}", clientId);
            } catch (Throwable t) {
                log.error("Failed to start the ONS push consumer, clientId={}", clientId);
                throw new ONSClientException(t);
            }
        }
        final ClientId clientId = pullConsumer.getClientId();
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to start the ONS push consumer, clientId={}", clientId);
                this.pullConsumer.startAsync().awaitRunning();
                log.info("ONS push consumer starts successfully, clientId={}", clientId);
                return;
            }
            log.warn("ONS push consumer has been started before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to start the ONS push consumer, clientId={}", clientId);
            throw new ONSClientException(t);
        }
    }

    @Override
    public void shutdown() {
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            final ClientId clientId = pushConsumer.getClientId();
            try {
                if (this.started.compareAndSet(false, true)) {
                    log.info("Begin to shutdown the ONS push consumer, clientId={}", clientId);
                    this.pullConsumer.stopAsync().awaitTerminated();
                    log.info("Shutdown ONS push consumer successfully, clientId={}", clientId);
                    return;
                }
                log.info("ONS push consumer has been shutdown before, clientId={}", clientId);
            } catch (Throwable t) {
                log.error("Failed to shutdown the ONS push consumer, clientId={}", clientId);
                throw new ONSClientException(t);
            }
        }
        final ClientId clientId = pullConsumer.getClientId();
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to shutdown the ONS push consumer, clientId={}", clientId);
                this.pullConsumer.stopAsync().awaitTerminated();
                log.info("Shutdown ONS push consumer successfully, clientId={}", clientId);
                return;
            }
            log.info("ONS push consumer has been shutdown before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to shutdown the ONS push consumer, clientId={}", clientId);
            throw new ONSClientException(t);
        }
    }
}
