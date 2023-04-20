package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Message;

/**
 * 发送顺序消息，应用可以自由选择发往哪个队列
 */
public interface MessageQueueSelector {
    /**
     * 发送顺序消息，队列选择方法
     *
     * @param queueTotal 队列总数
     * @param msg        需要发送的消息
     * @param arg        由send方法传入的自定义参数
     * @return 发往具体队列的Index
     */
    int select(final int queueTotal, final Message msg, final Object arg);
}
