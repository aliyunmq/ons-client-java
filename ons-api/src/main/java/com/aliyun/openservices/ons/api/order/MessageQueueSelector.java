package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Message;

/**
 * <p>Interface for sending ordered messages, allowing the application to freely choose which queue to send to.</p>
 * <p>发送顺序消息，应用可以自由选择发往哪个队列</p>
 */
public interface MessageQueueSelector {
    /**
     * <p>Method for selecting the queue when sending ordered messages.</p>
     *
     * @param queueTotal Total number of queues. / 队列总数。
     * @param msg        Message to be sent. / 需要发送的消息。
     * @param arg        Custom argument passed in by the send method. / 由send方法传入的自定义参数。
     * @return The index of the specific queue to send to. / 发往具体队列的Index。
     */
    int select(final int queueTotal, final Message msg, final Object arg);
}