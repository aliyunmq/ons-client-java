package org.apache.rocketmq.client.java.impl.consumer;

import static com.google.common.base.Preconditions.checkNotNull;

import com.aliyun.openservices.ons.api.OffsetStore;
import java.time.Duration;
import java.util.Optional;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullConsumerImplWithOffsetStore extends PullConsumerImpl {
    private static final Logger log = LoggerFactory.getLogger(PullConsumerImplWithOffsetStore.class);
    private OffsetStore offsetStore;

    public PullConsumerImplWithOffsetStore(ClientConfiguration clientConfiguration, String consumerGroup,
        boolean autoCommitEnabled, Duration autoCommitInterval, int maxCacheMessageCountTotalQueue,
        int maxCacheMessageCountEachQueue, int maxCacheMessageSizeInBytesTotalQueue,
        int maxCacheMessageSizeInBytesEachQueue) {
        super(clientConfiguration, consumerGroup, autoCommitEnabled, autoCommitInterval, maxCacheMessageCountTotalQueue,
            maxCacheMessageCountEachQueue, maxCacheMessageSizeInBytesTotalQueue, maxCacheMessageSizeInBytesEachQueue);
    }

    public void setOffsetStore(OffsetStore offsetStore) {
        this.offsetStore = checkNotNull(offsetStore, "OffsetStore is null, please set it");
    }

    public Optional<Long> readOffset(MessageQueueImpl mq) {
        if (null == offsetStore) {
            return Optional.empty();
        }
        final ONSTopicPartition partition = new ONSTopicPartition(mq);
        return offsetStore.readOffset(partition);
    }

    void tryPullMessageByMessageQueueImmediately(MessageQueueImpl mq, FilterExpression filterExpression) {
        final Optional<Long> optionalOffset = readOffset(mq);
        if (!optionalOffset.isPresent()) {
            final Optional<PullProcessQueue> pq = createProcessQueue(mq, filterExpression);
            pq.ifPresent(PullProcessQueue::pullMessageImmediately);
            return;
        }
        final Long offset = optionalOffset.get();
        log.info("Create process queue with offset={}, mq={}, clientId={}", offset, mq, clientId);
        final Optional<PullProcessQueue> pq = createProcessQueue(mq, filterExpression, offset);
        pq.ifPresent(PullProcessQueue::pullMessageImmediately);
    }
}
