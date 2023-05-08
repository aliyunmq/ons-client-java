package com.aliyun.openservices.ons.client;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.java.message.MessageImpl;

import static com.google.common.base.Preconditions.checkNotNull;

public class UtilAll {
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    public static final ClientServiceProvider provider = ClientServiceProvider.loadService();

    private static final Pattern NAME_SERVER_ENDPOINT_PATTERN = Pattern.compile("^(\\w+://|).*");

    private UtilAll() {
    }

    public static boolean validateNameServerEndpoint(String endpoint) {
        return StringUtils.isNoneBlank(endpoint) && NAME_SERVER_ENDPOINT_PATTERN.matcher(endpoint).matches();
    }

    public static org.apache.rocketmq.client.apis.message.Message convertMessage(Message message) {
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
        checkNotNull(body, "Message body should not be null");
        messageBuilder.setBody(body);
        final String tag = message.getTag();
        if (null != tag) {
            messageBuilder.setTag(tag);
        }
        final String key = message.getKey();
        if (null != key) {
            messageBuilder.setKeys(key);
        }
        final long startDeliverTime = message.getStartDeliverTime();
        if (startDeliverTime > 0) {
            messageBuilder.setDeliveryTimestamp(startDeliverTime);
        }
        return messageBuilder.build();
    }
}
