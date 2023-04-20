package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;


/**
 * 顺序消息生产者接口
 */
public interface OrderProducer extends Admin {

    /**
     * 发送顺序消息
     *
     * @param message     消息
     * @param shardingKey 顺序消息选择因子，发送方法基于shardingKey选择具体的消息队列
     * @return {@link SendResult} 消息发送结果，含消息Id
     */
    SendResult send(final Message message, final String shardingKey);
}
