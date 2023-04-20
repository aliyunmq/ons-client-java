package com.aliyun.openservices.ons.api;

import com.aliyun.openservices.ons.api.exception.ONSClientException;

/**
 * 发送消息异常上下文.
 */
public class OnExceptionContext {
    /**
     * 消息主题
     */
    private final String topic;

    /**
     * 消息ID
     */
    private final String messageId;

    /**
     * 异常对象, 包含详细的栈信息
     */
    private final ONSClientException exception;


    public OnExceptionContext(String topic, String messageId, ONSClientException exception) {
        this.messageId = messageId;
        this.topic = topic;
        this.exception = exception;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    public ONSClientException getException() {
        return exception;
    }
}
