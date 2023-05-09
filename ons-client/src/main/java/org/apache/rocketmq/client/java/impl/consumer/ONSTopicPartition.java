package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.api.Constants;
import com.aliyun.openservices.ons.api.TopicPartition;
import org.apache.rocketmq.client.java.route.MessageQueueImpl;

public class ONSTopicPartition extends TopicPartition {
    private final MessageQueueImpl messageQueue;

    public ONSTopicPartition(MessageQueueImpl messageQueue) {
        super(messageQueue.getTopic(), messageQueue.getBroker().getName() +
            Constants.TOPIC_PARTITION_SEPARATOR + messageQueue.getQueueId());
        this.messageQueue = messageQueue;
    }

    public MessageQueueImpl getMessageQueue() {
        return messageQueue;
    }
}
