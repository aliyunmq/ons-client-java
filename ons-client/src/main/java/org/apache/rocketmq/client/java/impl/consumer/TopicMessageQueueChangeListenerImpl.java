package org.apache.rocketmq.client.java.impl.consumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.apis.message.MessageQueue;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicMessageQueueChangeListenerImpl implements TopicMessageQueueChangeListener {
    private static final Logger log = LoggerFactory.getLogger(TopicMessageQueueChangeListenerImpl.class);

    private final PullConsumerImplWithOffsetStore pullConsumer;
    private final Map<String, FilterExpression> filterExpressionTable;

    public TopicMessageQueueChangeListenerImpl(PullConsumerImplWithOffsetStore pullConsumer,
        Map<String, FilterExpression> filterExpressionTable) {
        this.pullConsumer = pullConsumer;
        this.filterExpressionTable = filterExpressionTable;
    }

    @Override
    public void onChanged(String topic, Set<MessageQueue> messageQueues) {
        synchronized (pullConsumer) {
            final ClientId clientId = pullConsumer.getClientId();
            Map<MessageQueue, FilterExpression> latestSubscriptions = new HashMap<>();
            Map<MessageQueueImpl, FilterExpression> subscriptions = pullConsumer.getSubscriptions();
            Set<MessageQueue> addedMqs = new HashSet<>();
            for (MessageQueue mq : messageQueues) {
                final FilterExpression expression = filterExpressionTable.get(topic);
                if (null == expression) {
                    log.error("Expression doesn't exist, topic={}, clientId={}", topic, clientId);
                    return;
                }
                latestSubscriptions.put(mq, expression);
                if (!subscriptions.containsKey((MessageQueueImpl) mq)) {
                    addedMqs.add(mq);
                }
            }
            pullConsumer.assign(latestSubscriptions);
            for (MessageQueue mq : addedMqs) {
                final Optional<Long> optionalOffset = pullConsumer.readOffset((MessageQueueImpl) mq);
                if (!optionalOffset.isPresent()) {
                    try {
                        pullConsumer.seekToEnd(mq);
                    } catch (Throwable t) {
                        log.error("Failed to seek to the latest offset, mq={}, clientId={}", mq, clientId, t);
                        continue;
                    }
                    continue;
                }
                final Long offset = optionalOffset.get();
                try {
                    pullConsumer.seek(mq, offset);
                } catch (Throwable t) {
                    log.error("Failed to seek offset, mq={}, offset={}, clientId={}", mq, offset, clientId, t);
                }
            }
        }
    }
}