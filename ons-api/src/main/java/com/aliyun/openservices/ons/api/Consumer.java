package com.aliyun.openservices.ons.api;

/**
 * 消息消费者接口，用来订阅消息
 */
public interface Consumer extends Admin {
    /**
     * 订阅消息
     *
     * @param topic         消息主题
     * @param subExpression 订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。只支持或运算<br>
     *                      eg: "tag1 || tag2 || tag3"<br>
     *                      如果subExpression等于null或者*，则表示全部订阅
     * @param listener      消息回调监听器
     */
    void subscribe(final String topic, final String subExpression, final MessageListener listener);

    /**
     * 订阅消息，可以使用SQL表达式对消息进行过滤，请注意，SQL表达式过滤只针对MQ铂金版用户，公网服务暂时不支持。
     *
     * @param topic    消息主题
     * @param selector 订阅消息选择器（可空，表示不做过滤），ONS服务器依据此选择器中的表达式进行过滤。
     *                 目前支持两种表达式语法：{@link ExpressionType#TAG}, {@link ExpressionType#SQL92}
     *                 其中，TAG过滤的效果和上面的接口一致
     * @param listener 消息回调监听器
     */
    void subscribe(final String topic, final MessageSelector selector, final MessageListener listener);

    /**
     * 仅对广播消费模式生效，用户可以使用 {@link OffsetStore} 来定制广播模式下每个队列的消费起始位点以及进行位点的持久化。
     *
     * @param offsetStore 用于读取消费起始位点和位点的持久化。
     */
    void setOffsetStore(OffsetStore offsetStore);

    /**
     * 针对单机的消费限流。
     *
     * @param topic            被限流的 topic
     * @param permitsPerSecond topic 被限流的每秒消费速率
     */
    void rateLimit(String topic, double permitsPerSecond);

    /**
     * 取消某个topic订阅
     *
     * @param topic 要取消的主题.
     */
    void unsubscribe(final String topic);
}
