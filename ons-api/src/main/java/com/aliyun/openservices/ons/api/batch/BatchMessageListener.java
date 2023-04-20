package com.aliyun.openservices.ons.api.batch;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import java.util.List;

/**
 * <p>Batch message listener. BatchConsumer registers message listeners to consume messages 
 * in batches.</p>
 * <p>批量消息监听器，BatchConsumer注册消息监听器来批量地消费消息。</p>
 */
public interface BatchMessageListener {
    /**
     * <p>Batch message consumption interface, implemented by the application.</p>
     * <p>注意: 网络抖动等不稳定的情况可能会产生重复消息。业务中对于重复消息的处理需要做到幂等性。</p>
     * <p>BatchConsuming interface implemented by the application.<br>
     * Note: Unstable network connections among other issues may cause duplicate messages.
     * It's necessary for the business to ensure idempotence when dealing with duplicate messages.</p>
     *
     * @param messages A batch of messages / 一批消息
     * @param context  Consumption context / 消费上下文
     * @return {@link Action} The consumption result. If an exception is thrown by the application 
     *         or returns a null equivalent to returning Action.ReconsumeLater 
     *         / 消费结果。如果应用抛出异常或者返回Null等价于返回Action.ReconsumeLater
     * @see <a href="https://help.aliyun.com/document_detail/44397.html">如何做到消费幂等</a>
     */
    Action consume(final List<Message> messages, final ConsumeContext context);
}