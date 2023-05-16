package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.TopicPartition;
import com.aliyun.openservices.ons.client.UtilAll;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;

public class ONSTopicPartition extends TopicPartition {
    private final MessageQueueImpl messageQueue;

    public ONSTopicPartition(MessageQueueImpl messageQueue) {
        super(messageQueue.getTopic(), UtilAll.brokerNameQueueIdToPartition(messageQueue.getBroker().getName(),
            messageQueue.getQueueId()));
        this.messageQueue = messageQueue;
    }

    public MessageQueueImpl getMessageQueue() {
        return messageQueue;
    }
}
