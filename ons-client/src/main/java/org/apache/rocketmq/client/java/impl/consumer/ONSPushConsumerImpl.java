package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.client.UtilAll;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.apis.message.MessageQueue;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ONSPushConsumerImpl extends ONSPushConsumer implements Consumer {
    private static final Logger log = LoggerFactory.getLogger(ONSPushConsumerImpl.class);

    private final Map<String, FilterExpression> filterExpressionTable;
    private final Map<String, MessageListener> messageListenerTable;

    public ONSPushConsumerImpl(final Properties properties) {
        super(properties);
        this.filterExpressionTable = new HashMap<>();
        this.messageListenerTable = new HashMap<>();
    }

    @Override
    public void subscribe(String topic, String subExpression, MessageListener listener) {
        final MessageSelector selector = MessageSelector.byTag(subExpression);
        subscribe(topic, selector, listener);
    }

    @Override
    public void subscribe(String topic, MessageSelector selector, MessageListener listener) {
        if (StringUtils.isBlank(topic)) {
            throw new ONSClientException("Topic is blank unexpectedly, please set it");
        }
        if (null == listener) {
            throw new ONSClientException("Listener is null, please set it");
        }
        FilterExpression expression = UtilAll.convertFilterExpression(selector);
        filterExpressionTable.put(topic, expression);
        messageListenerTable.put(topic, listener);
        if (MessageModel.BROADCASTING.equals(this.messageModel)) {
            return;
        }
        pushConsumer.messageListener = messageView -> {
            final String messageTopic = messageView.getTopic();
            final MessageListener messageListener = messageListenerTable.get(messageTopic);
            if (null == messageListener) {
                log.error("MessageListener is null, message={}", messageView);
                throw new ONSClientException("MessageListener is null");
            }
            final ConsumeContext context = new ConsumeContext();
            final Action action = messageListener.consume(UtilAll.convertMessage(messageView), context);
            if (null == action) {
                return null;
            }
            switch (action) {
                case CommitMessage:
                    return ConsumeResult.SUCCESS;
                case ReconsumeLater:
                default:
                    return ConsumeResult.FAILURE;
            }
        };
        try {
            pushConsumer.subscriptionExpressions.put(topic, expression);
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void start() {
        super.start();
        if (MessageModel.BROADCASTING.equals(this.messageModel)) {
            assert pullConsumer != null;
            final ClientId clientId = pullConsumer.getClientId();
            for (Map.Entry<String, FilterExpression> entry : filterExpressionTable.entrySet()) {
                final String topic = entry.getKey();
                try {
                    final TopicMessageQueueChangeListenerImpl listener = new TopicMessageQueueChangeListenerImpl();
                    pullConsumer.registerMessageQueueChangeListenerByTopic(topic, listener);
                } catch (Throwable t) {
                    log.error("Failed to register message queue listener, clientId={}", clientId, t);
                }
            }
            this.messagePollingExecutor.submit(new MessagePollingTask());
        }
    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {
        if (null == pullConsumer) {
            return;
        }
        pullConsumer.setOffsetStore(offsetStore);
    }

    @Override
    public void unsubscribe(String topic) {
        pushConsumer.unsubscribe(topic);
    }

    class TopicMessageQueueChangeListenerImpl implements TopicMessageQueueChangeListener {
        @Override
        public void onChanged(String topic, Set<MessageQueue> messageQueues) {
            assert pullConsumer != null;
            final ClientId clientId = pullConsumer.getClientId();
            Map<MessageQueue, FilterExpression> filterExpressions = new HashMap<>();
            for (MessageQueue mq : messageQueues) {
                final FilterExpression expression = filterExpressionTable.get(topic);
                if (null == expression) {
                    log.error("Expression doesn't exist, topic={}, clientId={}", topic, clientId);
                    return;
                }
                filterExpressions.put(mq, expression);
            }
            pullConsumer.assign(filterExpressions);
            for (MessageQueue mq : messageQueues) {
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

    class MessagePollingTask implements Runnable {
        @Override
        public void run() {
            assert pullConsumer != null;
            final ClientId clientId = pullConsumer.getClientId();
            while (started.get()) {
                try {
                    final List<MessageView> messageViews = pullConsumer.poll(Duration.ofSeconds(3));
                    for (MessageView messageView : messageViews) {
                        final String topic = messageView.getTopic();
                        final MessageListener listener = messageListenerTable.get(topic);
                        if (null == listener) {
                            log.error("MessageListener is null, message={}", messageView);
                            continue;
                        }
                        final Message message = UtilAll.convertMessage(messageView);
                        final ConsumeContext context = new ConsumeContext();
                        polledMessageConsumptionExecutor.submit(() -> {
                            try {
                                listener.consume(message, context);
                            } catch (Throwable t) {
                                log.error("Exception raised while consuming message, clientId={}", clientId, t);
                            }
                        });
                    }
                } catch (Throwable t) {
                    log.error("Exception raised while polling message, clientId={}", clientId, t);
                }
            }
        }
    }
}
