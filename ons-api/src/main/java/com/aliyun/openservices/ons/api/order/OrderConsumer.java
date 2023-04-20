package com.aliyun.openservices.ons.api.order;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.MessageSelector;
import com.aliyun.openservices.ons.api.OffsetStore;


/**
 * 顺序消息消费者接口
 */
public interface OrderConsumer extends Admin {
    /**
     * 启动服务
     */
    @Override
    void start();

    /**
     * 关闭服务
     */
    @Override
    void shutdown();

    /**
     * 订阅消息
     *
     * @param topic         消息主题
     * @param subExpression 订阅过滤表达式字符串，broker依据此表达式进行过滤。目前只支持或运算<br> eg: "tag1 || tag2 || tag3"<br>
     *                      如果subExpression等于null或者*，则表示全部订阅
     * @param listener      消息回调监听器，客户端接收到消息后传给消息回调监听器进行消费
     */
    void subscribe(final String topic, final String subExpression, final MessageOrderListener listener);

    /**
     * 仅对广播消费模式生效，用户可以使用 {@link OffsetStore} 来定制广播模式下每个队列的消费起始位点以及进行位点的持久化。
     *
     * @param offsetStore 用于读取消费起始位点和位点的持久化。
     */
    void setOffsetStore(OffsetStore offsetStore);

    /**
     * 订阅消息，可以使用SQL表达式对消息进行过滤，请注意，SQL表达式过滤只针对MQ铂金版用户，公网服务暂时不支持。
     *
     * @param topic    消息主题
     * @param selector 订阅消息选择器（可空，表示不做过滤），ONS服务器依据此选择器中的表达式进行过滤。
     *                 目前支持两种表达式语法：{@link com.aliyun.openservices.ons.api.ExpressionType#TAG},
     *                 {@link com.aliyun.openservices.ons.api.ExpressionType#SQL92}
     *                 其中，TAG过滤的效果和上面的接口一致
     * @param listener 消息回调监听器
     */
    void subscribe(final String topic, final MessageSelector selector, final MessageOrderListener listener);

    /**
     * 针对单机的消费限流，顺序消费者的单机消费限流对本地重试消息是透明的
     *
     * @param topic            被限流的 topic
     * @param permitsPerSecond topic 被限流的每秒消费速率
     */
    void rateLimit(String topic, double permitsPerSecond);
}
