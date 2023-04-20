package com.aliyun.openservices.ons.api.batch;

import com.aliyun.openservices.ons.api.Admin;

/**
 * <p>Batch message consumer for subscribing messages in batches.</p>
 * <p>批量消息消费者，用来通过批量的方式订阅消息。</p>
 */
public interface BatchConsumer extends Admin {

    /**
     * <p>Subscribe to messages.</p>
     * <p>订阅消息。</p>
     *
     * @param topic Message topic / 消息主题
     * @param subExpression Subscription filter expression string. The ONS server filters
     *                      based on this expression. Supports OR operation only. <br>
     *                      For example: "tag1 || tag2 || tag3"<br>
     *                      If subExpression is null or *, it means subscribe to all. /
     *                      订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。
     *                      只支持或运算。<br>例如："tag1 || tag2 || tag3"<br>
     *                      如果subExpression等于null或者*，则表示全部订阅。
     * @param listener Message callback listener / 消息回调监听器
     */
    void subscribe(final String topic, final String subExpression,
        final BatchMessageListener listener);

    /**
     * <p>Unsubscribe from a specific topic.</p>
     * <p>取消某个topic的订阅。</p>
     *
     * @param topic Message topic / 消息主题
     */
    void unsubscribe(final String topic);
}