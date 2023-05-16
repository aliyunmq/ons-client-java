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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.message.MessageQueue;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.misc.ExecutorServices;
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
        // For clustering consumption mode.
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            try {
                if (this.started.compareAndSet(false, true)) {
                    log.info("Begin to start the ONS push consumer[clustering], clientId={}", clientId);
                    this.pushConsumer.startAsync().awaitRunning();
                    log.info("ONS push consumer[clustering] starts successfully, clientId={}", clientId);
                    return;
                }
                log.warn("ONS push consumer[clustering] has been started before, clientId={}", clientId);
                return;
            } catch (Throwable t) {
                log.error("Failed to start the ONS push consumer[clustering], clientId={}", clientId);
                throw new ONSClientException(t);
            }
        }
        // For broadcasting consumption mode.
        try {
            if (!this.started.compareAndSet(false, true)) {
                log.warn("ONS push consumer[broadcasting] has been started before, clientId={}", clientId);
                return;
            }
            log.info("Begin to start the ONS push consumer[broadcasting], clientId={}", clientId);
            this.pullConsumer.startAsync().awaitRunning();
            log.info("ONS push consumer[broadcasting] starts successfully, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to start the ONS push consumer[broadcasting], clientId={}", clientId);
            throw new ONSClientException(t);
        }
        for (Map.Entry<String, FilterExpression> entry : filterExpressionTable.entrySet()) {
            final String topic = entry.getKey();
            try {
                final TopicMessageQueueChangeListenerImpl listener = new TopicMessageQueueChangeListenerImpl(
                    pullConsumer, filterExpressionTable);
                pullConsumer.registerMessageQueueChangeListenerByTopic(topic, listener);
            } catch (Throwable t) {
                log.error("Failed to register message queue listener, clientId={}", clientId, t);
            }
        }
        this.messagePollingExecutor.submit(new MessagePollingTask());
    }

    public void shutdown() {
        // For clustering consumption mode.
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            try {
                if (this.started.compareAndSet(true, false)) {
                    log.info("Begin to shutdown the ONS push consumer[clustering], clientId={}", clientId);
                    this.pushConsumer.stopAsync().awaitTerminated();
                    log.info("Shutdown ONS push consumer[clustering] successfully, clientId={}", clientId);
                    return;
                }
                log.warn("ONS push consumer[clustering] has been shutdown before, clientId={}", clientId);
                return;
            } catch (Throwable t) {
                log.error("Failed to shutdown the ONS push consumer[clustering], clientId={}", clientId, t);
            }
        }
        // For broadcasting consumption mode.
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to shutdown the ONS push consumer[broadcasting], clientId={}", clientId);
                this.pullConsumer.stopAsync().awaitTerminated();
                messagePollingExecutor.shutdown();
                ExecutorServices.awaitTerminated(messagePollingExecutor);
                log.info("Shutdown ONS push consumer[broadcasting] successfully, clientId={}", clientId);
                return;
            }
            log.warn("ONS push consumer[broadcasting] has been shutdown before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to shutdown the ONS push consumer[clustering], clientId={}", clientId, t);
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
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            pushConsumer.unsubscribe(topic);
            return;
        }
        synchronized (pullConsumer) {
            final Map<MessageQueueImpl, FilterExpression> subscriptions = pullConsumer.getSubscriptions();
            Map<MessageQueue, FilterExpression> newSubscriptions = new HashMap<>();
            for (MessageQueueImpl mq : subscriptions.keySet()) {
                if (mq.getTopic().equals(topic)) {
                    continue;
                }
                newSubscriptions.put(mq, newSubscriptions.get(mq));
            }
            pullConsumer.assign(newSubscriptions);
        }
    }

    class MessagePollingTask implements Runnable {
        @Override
        public void run() {
            while (started.get()) {
                try {
                    final List<MessageView> messageViews = pullConsumer.poll(MESSAGE_BROADCASTING_POLLING_DURATION);
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
                            } finally {
                                pullConsumer.updateOffset(message.getTopicPartition(), message.getOffset());
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
