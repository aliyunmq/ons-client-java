package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.client.UtilAll;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.java.misc.ClientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ONSPushConsumerImpl extends ONSPushConsumer implements Consumer {
    private static final Logger log = LoggerFactory.getLogger(ONSPushConsumerImpl.class);

    public ONSPushConsumerImpl(final Properties properties) {
        super(properties);
    }

    @Override
    public void start() {
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

    @Override
    public void shutdown() {
        final ClientId clientId = pushConsumer.getClientId();
        try {
            if (this.started.compareAndSet(true, false)) {
                log.info("Begin to shutdown the ONS push consumer, clientId={}", clientId);
                this.pushConsumer.stopAsync().awaitTerminated();
                log.info("Shutdown ONS push consumer successfully, clientId={}", clientId);
                return;
            }
            log.info("ONS push consumer has been shutdown before, clientId={}", clientId);
        } catch (Throwable t) {
            log.error("Failed to shutdown the ONS push consumer, clientId={}", clientId);
            throw new ONSClientException(t);
        }
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
        pushConsumer.messageListener = messageView -> {
            if (!topic.equals(messageView.getTopic())) {
                return pushConsumer.messageListener.consume(messageView);
            }
            final ConsumeContext context = new ConsumeContext();
            final Action action = listener.consume(UtilAll.convertMessage(messageView), context);
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
        FilterExpression expression;
        switch (selector.getType()) {
            case TAG:
                expression = new FilterExpression(selector.getSubExpression(), FilterExpressionType.TAG);
                break;
            case SQL92:
            default:
                expression = new FilterExpression(selector.getSubExpression(), FilterExpressionType.SQL92);
        }
        try {
            pushConsumer.subscribe(topic, expression);
        } catch (Throwable t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void setOffsetStore(OffsetStore offsetStore) {

    }

    @Override
    public void unsubscribe(String topic) {
        pushConsumer.unsubscribe(topic);
    }
}
