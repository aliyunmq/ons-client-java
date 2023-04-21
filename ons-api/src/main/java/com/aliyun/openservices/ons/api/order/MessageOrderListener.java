package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Message;

/**
 * <p>Message listener, consumers register the message listener to subscribe to messages.</p>
 * <p>消息监听器，Consumer注册消息监听器来订阅消息。</p>
 */
public interface MessageOrderListener {
    /**
     * <p>Message consumption interface, to be implemented by the application.<br>
     * Note that network jitter and other unstable situations may cause duplicate messages.</p>
     * <p>消费消息接口，由应用实现。<br>
     * 需要注意的是，网络抖动等不稳定情况可能导致重复的消息。</p>
     *
     * @param message Message to be consumed. / 消息。
     * @param context Consumption context. / 消费上下文。
     * @return {@link OrderAction} Consumption result, if the application throws an exception or returns null,
     * it is equivalent to returning Action.ReconsumeLater.
     * @see <a href="https://help.aliyun.com/document_detail/44397.html">How to achieve consumption idempotence /
     * 如何做到消费幂等</a>
     */
    OrderAction consume(final Message message, final ConsumeOrderContext context);
}