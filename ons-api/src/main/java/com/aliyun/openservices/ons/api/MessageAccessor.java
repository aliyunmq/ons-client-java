package com.aliyun.openservices.ons.api;

import java.util.Properties;

public class MessageAccessor {

    private MessageAccessor() {
    }

    public static Message message(String topic, byte[] body, SystemProperties systemProperties,
                                  Properties userProperties) {
        return new Message(topic, body, systemProperties, userProperties);
    }
}
