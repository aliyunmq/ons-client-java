package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.client.UtilAll;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.apache.rocketmq.client.java.misc.ExecutorServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ONSOrderConsumerImpl extends ONSPushConsumer implements OrderConsumer {
    private static final Logger log = LoggerFactory.getLogger(ONSOrderConsumerImpl.class);

    private final Map<String, FilterExpression> filterExpressionTable;
    private final Map<String, MessageOrderListener> messageOrderListenerTable;

    public ONSOrderConsumerImpl(final Properties properties) {
        super(properties);
        this.filterExpressionTable = new HashMap<>();
        this.messageOrderListenerTable = new HashMap<>();
        // Broadcast consumption by order consumers will degenerate into unordered consumption.
        pushConsumer.getPushConsumerSettings().fifo = messageModel.equals(MessageModel.CLUSTERING);
    }

    @Override
    public void start() {
        // For clustering consumption mode.
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            try {
                if (this.started.compareAndSet(false, true)) {
                    log.info("Begin to start the ONS order consumer[clustering], clientId={}", clientId);
                    this.pushConsumer.startAsync().awaitRunning();
                    log.info("ONS order consumer[clustering] starts successfully, clientId={}", clientId);
                    return;
                }
                log.warn("ONS order consumer[clustering] has been started before, clientId={}", clientId);
                return;
            } catch (Throwable t) {
                log.error("Failed to start the ONS order consumer[clustering], clientId={}", clientId);
                throw new ONSClientException(t);
            }
        }
        // For broadcasting consumption mode.
        try {
            if (!this.started.compareAndSet(false, true)) {
                log.warn("ONS order consumer[broadcasting] has been started before, clientId={}", clientId);
                return;
            }
            log.info("Begin to start the ONS order consumer[broadcasting], clientId={}", clientId);
            this.pullConsumer.startAsync().awaitRunning();
            log.info("ONS order consumer[broadcasting] starts successfully, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to start the ONS order consumer[broadcasting], clientId={}", clientId);
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

    @Override
    public void shutdown() {
        // For clustering consumption mode.
        if (MessageModel.CLUSTERING.equals(this.messageModel)) {
            try {
                if (this.started.compareAndSet(true, false)) {
                    log.info("Begin to shutdown the ONS order consumer[clustering], clientId={}", clientId);
                    this.pushConsumer.stopAsync().awaitTerminated();
                    log.info("Shutdown ONS order consumer[clustering] successfully, clientId={}", clientId);
                    return;
                }
                log.warn("ONS order consumer[clustering] has been shutdown before, clientId={}", clientId);
                return;
            } catch (Throwable t) {
                log.error("Failed to shutdown the ONS order consumer[clustering], clientId={}", clientId, t);
            }
        }
        // For broadcasting consumption mode.
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to shutdown the ONS order consumer[broadcasting], clientId={}", clientId);
                this.pullConsumer.stopAsync().awaitTerminated();
                messagePollingExecutor.shutdown();
                ExecutorServices.awaitTerminated(messagePollingExecutor);
                log.info("Shutdown ONS order consumer[broadcasting] successfully, clientId={}", clientId);
                return;
            }
            log.warn("ONS order consumer[broadcasting] has been shutdown before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to shutdown the ONS order consumer[clustering], clientId={}", clientId, t);
        }
    }

    @Override
    public void subscribe(String topic, String subExpression, MessageOrderListener listener) {
        final MessageSelector selector = MessageSelector.byTag(subExpression);
        subscribe(topic, selector, listener);
    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {
        if (null == pullConsumer) {
            return;
        }
        pullConsumer.setOffsetStore(offsetStore);
    }

    @Override
    public void subscribe(String topic, MessageSelector selector, MessageOrderListener listener) {
        if (StringUtils.isBlank(topic)) {
            throw new ONSClientException("Topic is blank unexpectedly, please set it");
        }
        if (null == listener) {
            throw new ONSClientException("Listener is null, please set it");
        }
        FilterExpression expression = UtilAll.convertFilterExpression(selector);
        filterExpressionTable.put(topic, expression);
        messageOrderListenerTable.put(topic, listener);
        pushConsumer.messageListener = messageView -> {
            if (!topic.equals(messageView.getTopic())) {
                return pushConsumer.messageListener.consume(messageView);
            }
            final ConsumeOrderContext context = new ConsumeOrderContext();
            final OrderAction action = listener.consume(UtilAll.convertMessage(messageView), context);
            if (null == action) {
                return null;
            }
            switch (action) {
                case Success:
                    return ConsumeResult.SUCCESS;
                case Suspend:
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

    class MessagePollingTask implements Runnable {
        @Override
        public void run() {
            final ClientId clientId = pullConsumer.getClientId();
            while (started.get()) {
                try {
                    final List<MessageView> messageViews = pullConsumer.poll(MESSAGE_BROADCASTING_POLLING_DURATION);
                    for (MessageView messageView : messageViews) {
                        final String topic = messageView.getTopic();
                        final MessageOrderListener listener = messageOrderListenerTable.get(topic);
                        if (null == listener) {
                            log.error("MessageListener is null, message={}", messageView);
                            continue;
                        }
                        final Message message = UtilAll.convertMessage(messageView);
                        final ConsumeOrderContext context = new ConsumeOrderContext();
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
