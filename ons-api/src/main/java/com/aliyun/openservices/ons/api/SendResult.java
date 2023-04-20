package com.aliyun.openservices.ons.api;

/**
 * 发送结果.
 */
public class SendResult {
    /**
     * 已发送消息的主题
     */
    private final String topic;
    /**
     * 已发送消息的ID
     */
    private final String messageId;


    public SendResult(String topic, String messageId) {
        this.topic = topic;
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    /**
     * 序列化发送结果.
     *
     * @return 发送结果的String表示.
     */
    @Override
    public String toString() {
        return "SendResult[topic=" + topic + ", messageId=" + messageId + ']';
    }
}
