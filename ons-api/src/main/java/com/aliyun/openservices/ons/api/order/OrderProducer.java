package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;

/**
 * Ordered message producer interface.
 * <p>顺序消息生产者接口。
 */
public interface OrderProducer extends Admin {

    /**
     * Send ordered messages.
     * <p>发送顺序消息。
     *
     * @param message     Message. / 消息。
     * @param shardingKey Ordered message selection factor, the send method selects the specific message queue based on
     *                    the shardingKey. / 顺序消息选择因子，发送方法基于shardingKey选择具体的消息队列。
     * @return {@link SendResult} Message sending result, including message Id. / 消息发送结果，含消息Id。
     */
    SendResult send(final Message message, final String shardingKey);
}