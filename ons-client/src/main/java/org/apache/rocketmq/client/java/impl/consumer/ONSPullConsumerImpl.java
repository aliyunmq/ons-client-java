package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PullConsumer;
import com.aliyun.openservices.ons.api.TopicPartition;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.client.ClientAbstract;
import com.aliyun.openservices.ons.client.UtilAll;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageQueue;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ONSPullConsumerImpl extends ClientAbstract implements PullConsumer {
    private static final Logger log = LoggerFactory.getLogger(ONSPullConsumerImpl.class);

    private static final int DEFAULT_MAX_CACHED_MESSAGE_AMOUNT = 10000;
    private static final int MAX_MAX_CACHED_MESSAGE_AMOUNT = 50000;
    private static final int MIN_MAX_CACHED_MESSAGE_AMOUNT = 100;

    private static final int DEFAULT_MAX_CACHED_MESSAGE_SIZE_IN_MIB = 100;
    private static final int MAX_MAX_CACHED_MESSAGE_SIZE_IN_MIB = 1024;
    private static final int MIN_MAX_CACHED_MESSAGE_SIZE_IN_MIB = 16;

    private static final long DEFAULT_AUTO_COMMIT_INTERVAL_MILLIS = 5000;
    private static final long MIN_AUTO_COMMIT_INTERVAL_MILLIS = 1000;

    private final PullConsumerImpl pullConsumer;

    public ONSPullConsumerImpl(Properties properties) {
        super(properties);
        final String consumerGroup = properties.getProperty(PropertyKeyConst.GROUP_ID);
        if (StringUtils.isBlank(consumerGroup)) {
            throw new ONSClientException("Group id is blank, please set it.");
        }

        final String maxCachedMessageAmountProp = properties.getProperty(PropertyKeyConst.MaxCachedMessageAmount);
        int maxCachedMessageAmount = DEFAULT_MAX_CACHED_MESSAGE_AMOUNT;
        if (StringUtils.isNoneBlank(maxCachedMessageAmountProp)) {
            maxCachedMessageAmount = Integer.parseInt(maxCachedMessageAmountProp);
            maxCachedMessageAmount = Math.min(maxCachedMessageAmount, MAX_MAX_CACHED_MESSAGE_AMOUNT);
            maxCachedMessageAmount = Math.max(maxCachedMessageAmount, MIN_MAX_CACHED_MESSAGE_AMOUNT);
        }

        final String maxCachedMessageSizeInMiBProp = properties.getProperty(PropertyKeyConst.MaxCachedMessageSizeInMiB);
        int maxCachedMessageSizeInMib = DEFAULT_MAX_CACHED_MESSAGE_SIZE_IN_MIB;
        if (StringUtils.isNoneBlank(maxCachedMessageSizeInMiBProp)) {
            maxCachedMessageSizeInMib = Integer.parseInt(maxCachedMessageSizeInMiBProp);
            maxCachedMessageSizeInMib = Math.min(maxCachedMessageSizeInMib, MAX_MAX_CACHED_MESSAGE_SIZE_IN_MIB);
            maxCachedMessageSizeInMib = Math.max(maxCachedMessageSizeInMib, MIN_MAX_CACHED_MESSAGE_SIZE_IN_MIB);
        }

        boolean autoCommit = false;
        String autoCommitString = properties.getProperty(PropertyKeyConst.AUTO_COMMIT);
        autoCommit = Boolean.parseBoolean(autoCommitString);

        String autoCommitIntervalMillisProp = properties.getProperty(PropertyKeyConst.AUTO_COMMIT_INTERVAL_MILLIS);
        long autoCommitIntervalMillis = DEFAULT_AUTO_COMMIT_INTERVAL_MILLIS;
        if (StringUtils.isNoneBlank(autoCommitIntervalMillisProp)) {
            autoCommitIntervalMillis = Long.parseLong(autoCommitIntervalMillisProp);
            autoCommitIntervalMillis = Math.max(MIN_AUTO_COMMIT_INTERVAL_MILLIS, autoCommitIntervalMillis);
        }

        this.pullConsumer = new PullConsumerImpl(clientConfiguration, consumerGroup, autoCommit,
            Duration.ofMillis(autoCommitIntervalMillis), maxCachedMessageAmount, Integer.MAX_VALUE,
            Integer.MAX_VALUE, 1024 * 1024 * maxCachedMessageSizeInMib);
    }

    @Override
    public void start() {
        final ClientId clientId = pullConsumer.getClientId();
        try {
            if (this.started.compareAndSet(false, true)) {
                log.info("Begin to start the ONS pull consumer, clientId={}", clientId);
                this.pullConsumer.startAsync().awaitRunning();
                log.info("ONS pull consumer starts successfully, clientId={}", clientId);
                return;
            }
            log.warn("ONS pull consumer has been started before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to start the ONS pull consumer, clientId={}", clientId);
            throw new ONSClientException(t);
        }
    }

    public void shutdown() {
        final ClientId clientId = pullConsumer.getClientId();
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to shutdown the ONS pull consumer, clientId={}", clientId);
                this.pullConsumer.stopAsync().awaitTerminated();
                log.info("Shutdown ONS pull consumer successfully, clientId={}", clientId);
                return;
            }
            log.warn("ONS pull consumer has been shutdown before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to shutdown the ONS pull consumer, clientId={}", clientId);
            throw new ONSClientException(t);
        }
    }

    @Override
    public Set<TopicPartition> topicPartitions(String topic) {
        try {
            final Collection<MessageQueue> mqs = pullConsumer.fetchMessageQueues(topic);
            Set<TopicPartition> topicPartitions = new HashSet<>();
            for (MessageQueue mq : mqs) {
                final ONSTopicPartition partition = new ONSTopicPartition((MessageQueueImpl) mq);
                topicPartitions.add(partition);
            }
            return topicPartitions;
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void assign(Collection<TopicPartition> topicPartitions) {
        Set<MessageQueue> messageQueues = new HashSet<>();
        for (TopicPartition partition : topicPartitions) {
            final ONSTopicPartition topicPartition = (ONSTopicPartition) partition;
            messageQueues.add(topicPartition.getMessageQueue());
        }
        pullConsumer.assign(messageQueues);
    }

    @Override
    public void registerTopicPartitionChangedListener(String topic, TopicPartitionChangeListener callback) {
        try {
            pullConsumer.registerMessageQueueChangeListenerByTopic(topic, (topic1, messageQueues) -> {
                Set<TopicPartition> topicPartitions = new HashSet<>();
                for (MessageQueue queue : messageQueues) {
                    final ONSTopicPartition partition = new ONSTopicPartition((MessageQueueImpl) queue);
                    topicPartitions.add(partition);
                }
                callback.onChanged(topicPartitions);
            });
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public List<Message> poll(long timeout) {
        try {
            final List<MessageView> messageViews = pullConsumer.poll(Duration.ofMillis(timeout));
            List<Message> messages = new ArrayList<>();
            for (MessageView messageView : messageViews) {
                final Message message = UtilAll.convertMessage(messageView);
                messages.add(message);
            }
            return messages;
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void seek(TopicPartition topicPartition, long offset) {
        final ONSTopicPartition partition = (ONSTopicPartition) topicPartition;
        pullConsumer.seek(partition.getMessageQueue(), offset);
    }

    @Override
    public void seekToBeginning(TopicPartition topicPartition) {
        final ONSTopicPartition partition = (ONSTopicPartition) topicPartition;
        try {
            pullConsumer.seekToBegin(partition.getMessageQueue());
        } catch (ClientException e) {
            throw new ONSClientException(e);
        }
    }

    @Override
    public void seekToEnd(TopicPartition topicPartition) {
        final ONSTopicPartition partition = (ONSTopicPartition) topicPartition;
        try {
            pullConsumer.seekToEnd(partition.getMessageQueue());
        } catch (ClientException e) {
            throw new ONSClientException(e);
        }
    }

    @Override
    public void pause(Collection<TopicPartition> topicPartitions) {
        Set<MessageQueue> messageQueues = new HashSet<>();
        for (TopicPartition partition : topicPartitions) {
            final ONSTopicPartition topicPartition = (ONSTopicPartition) partition;
            messageQueues.add(topicPartition.getMessageQueue());
        }
        pullConsumer.pause(messageQueues);
    }

    @Override
    public void resume(Collection<TopicPartition> topicPartitions) {
        Set<MessageQueue> messageQueues = new HashSet<>();
        for (TopicPartition partition : topicPartitions) {
            final ONSTopicPartition topicPartition = (ONSTopicPartition) partition;
            messageQueues.add(topicPartition.getMessageQueue());
        }
        pullConsumer.resume(messageQueues);
    }

    @Override
    public Long offsetForTimestamp(TopicPartition topicPartition, Long timestamp) {
        final ONSTopicPartition onsTopicPartition = (ONSTopicPartition) topicPartition;
        try {
            final MessageQueueImpl queue = onsTopicPartition.getMessageQueue();
            final Optional<Long> optional = pullConsumer.offsetForTimestamp(queue, timestamp);
            return optional.orElse(-1L);
        } catch (ClientException e) {
            throw new ONSClientException(e);
        }
    }

    @Override
    public Long committed(TopicPartition topicPartition) {
        final ONSTopicPartition partition = (ONSTopicPartition) topicPartition;
        try {
            final Optional<Long> optional = pullConsumer.committed(partition.getMessageQueue());
            return optional.orElse(-1L);
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void commitSync() {
        try {
            pullConsumer.commit();
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }
}
