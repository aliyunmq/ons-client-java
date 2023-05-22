package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.client.ClientAbstract;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.java.impl.SettingsAccessor;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.apache.rocketmq.client.java.misc.ThreadFactoryImpl;

public class ONSPushConsumer extends ClientAbstract {
    protected static final Duration MESSAGE_BROADCASTING_POLLING_DURATION = Duration.ofSeconds(3);
    private static final int MAX_CACHED_MESSAGES_QUANTITY = 50000;
    private static final int MIN_CACHED_MESSAGES_QUANTITY = 100;
    private static final int DEFAULT_CACHED_MESSAGES_QUANTITY = 5000;

    private static final int MIN_CACHED_MESSAGE_MEMORY_IN_MIB = 16;
    private static final int MAX_CACHED_MESSAGE_MEMORY_IN_MIB = 2048;
    private static final int DEFAULT_CACHED_MESSAGE_MEMORY_IN_MIB = 512;

    private static final int DEFAULT_CONSUMPTION_THREADS_AMOUNT = 20;
    private static final int CONSUMPTION_THREADS_MAX_AMOUNT = 1000;

    protected final Map<String, FilterExpression> filterExpressions;
    protected final PushConsumerImpl pushConsumer;
    protected final PullConsumerImplWithOffsetStore pullConsumer;
    protected final ClientId clientId;
    protected final ThreadPoolExecutor messagePollingExecutor;
    protected final ThreadPoolExecutor polledMessageConsumptionExecutor;

    protected final MessageModel messageModel;

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
        this.polledMessageConsumptionExecutor = new ThreadPoolExecutor(consumptionThreadsAmount,
            consumptionThreadsAmount, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactoryImpl("PolledMessageConsumption"));
        final String messageModelProp = properties.getProperty(PropertyKeyConst.MessageModel,
            PropertyValueConst.DEFAULT_MESSAGE_MODEL);
        this.messageModel = MessageModel.valueOf(messageModelProp);
        this.filterExpressions = new HashMap<>();

        // Message listener here will be overwritten.
        MessageListener messageListener = messageView -> ConsumeResult.SUCCESS;
        this.pushConsumer = new PushConsumerImpl(clientConfiguration, consumerGroup, new HashMap<>(),
            messageListener, maxCachedMessagesQuantity, maxCachedMessageSizeInMib * 1024 * 1024,
            consumptionThreadsAmount);
        final String maxReconsumeTimesProp = properties.getProperty(PropertyKeyConst.MaxReconsumeTimes);
        if (StringUtils.isNoneBlank(maxReconsumeTimesProp)) {
            int maxConsumptionAttempts = 1 + Integer.parseInt(maxReconsumeTimesProp);
            final RetryPolicyImpl impl = new RetryPolicyImpl(maxConsumptionAttempts);
            SettingsAccessor.setRetryPolicy(pushConsumer.getPushConsumerSettings(), impl);
        }

        // For broadcasting consumption mode
        this.pullConsumer = new PullConsumerImplWithOffsetStore(clientConfiguration, consumerGroup, false,
            Duration.ofSeconds(5), maxCachedMessagesQuantity, Integer.MAX_VALUE,
            maxCachedMessageSizeInMib * 1024 * 1024, Integer.MAX_VALUE);

        this.clientId = MessageModel.CLUSTERING.equals(this.messageModel) ? pushConsumer.getClientId() :
            pullConsumer.getClientId();

        this.messagePollingExecutor = new ThreadPoolExecutor(1, 1, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryImpl("MessagePolling"));
    }
}
