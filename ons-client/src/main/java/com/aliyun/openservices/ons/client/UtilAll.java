package com.aliyun.openservices.ons.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.aliyun.openservices.ons.api.Constants;
import com.aliyun.openservices.ons.api.ExpressionType;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageAccessor;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.SystemProperties;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.message.MessageViewImpl;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;

public class UtilAll {
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    public static final ClientServiceProvider provider = ClientServiceProvider.loadService();

    private static final Pattern NAME_SERVER_ENDPOINT_PATTERN = Pattern.compile("^(\\w+://|).*");
    private static final String MESSAGE_KEY_SEPARATOR = " ";

    private UtilAll() {
    }

    public static boolean validateNameServerEndpoint(String endpoint) {
        return StringUtils.isNoneBlank(endpoint) && NAME_SERVER_ENDPOINT_PATTERN.matcher(endpoint).matches();
    }

    public static FilterExpression convertFilterExpression(MessageSelector selector) {
        if (ExpressionType.TAG.equals(selector.getType())) {
            return new FilterExpression(selector.getSubExpression());
        }
        return new FilterExpression(selector.getSubExpression(), FilterExpressionType.SQL92);
    }

    public static String brokerNameQueueIdToPartition(String brokerName, int queueId) {
        return brokerName + Constants.TOPIC_PARTITION_SEPARATOR + queueId;
    }

    public static Message convertMessage(MessageView messageView) {
        MessageViewImpl impl = (MessageViewImpl) messageView;
        final String messageId = impl.getMessageId().toString();
        byte[] body = new byte[impl.getBody().remaining()];
        impl.getBody().get(body);
        final String tag = impl.getTag().isPresent() ? impl.getTag().get() : null;
        final Collection<String> keys = impl.getKeys();
        final String messageGroup = impl.getMessageGroup().isPresent() ? impl.getMessageGroup().get() : null;
        final int reconsumeTimes = impl.getDeliveryAttempt() - 1;
        final Long deliveryTimestamp = impl.getDeliveryTimestamp().isPresent() ?
            impl.getDeliveryTimestamp().get() : null;
        final String bornHost = impl.getBornHost();
        final long bornTimestamp = impl.getBornTimestamp();

        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.setTag(tag);
        systemProperties.setKey(Joiner.on(MESSAGE_KEY_SEPARATOR).join(keys));
        systemProperties.setMsgId(messageId);
        systemProperties.setShardingKey(messageGroup);
        systemProperties.setReconsumeTimes(reconsumeTimes);
        systemProperties.setBornHost(bornHost);
        systemProperties.setBornTimestamp(bornTimestamp);
        if (null != deliveryTimestamp) {
            systemProperties.setStartDeliverTime(deliveryTimestamp);
        }
        systemProperties.setPartitionOffset(impl.getOffset());
        final MessageQueueImpl mq = impl.getMessageQueue();
        systemProperties.setPartition(UtilAll.brokerNameQueueIdToPartition(mq.getBroker().getName(), mq.getQueueId()));
        final Properties userProperties = new Properties();
        userProperties.putAll(impl.getProperties());
        return MessageAccessor.message(messageView.getTopic(), body, systemProperties, userProperties);
    }

    public static org.apache.rocketmq.client.apis.message.Message convertMessage(Message message) {
        return convertMessage(message, null);
    }

    public static org.apache.rocketmq.client.apis.message.Message convertMessage(Message message, String messageGroup) {
        if (null == message) {
            throw new ONSClientException("Message should not be null");
        }
        checkNotNull(message, "Message should not be null");
        MessageBuilder messageBuilder = provider.newMessageBuilder();
        final String topic = message.getTopic();
        if (StringUtils.isAnyBlank(topic)) {
            throw new ONSClientException("Message topic has blank, please set it.");
        }
        messageBuilder.setTopic(topic);
        final Properties userProperties = message.getUserProperties();
        for (String propertyName : userProperties.stringPropertyNames()) {
            messageBuilder.addProperty(propertyName, userProperties.getProperty(propertyName));
        }
        final byte[] body = message.getBody();
        if (null == body) {
            throw new ONSClientException("Message body should not be null");
        }
        messageBuilder.setBody(body);
        final String tag = message.getTag();
        if (StringUtils.isNotBlank(tag)) {
            messageBuilder.setTag(tag);
        }
        final String key = message.getKey();
        if (StringUtils.isNotBlank(key)) {
            messageBuilder.setKeys(key);
        }
        final long startDeliverTime = message.getStartDeliverTime();
        if (startDeliverTime > 0) {
            messageBuilder.setDeliveryTimestamp(startDeliverTime);
        }
        if (StringUtils.isNotBlank(messageGroup)) {
            messageBuilder.setMessageGroup(messageGroup);
        }
        return messageBuilder.build();
    }
}
