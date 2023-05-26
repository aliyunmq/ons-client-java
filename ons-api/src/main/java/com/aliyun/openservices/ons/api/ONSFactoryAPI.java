package com.aliyun.openservices.ons.api;

import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import java.util.Properties;

/**
 * MQ 各类Client构造的工程接口，用于创建Producer和Consumer
 */
public interface ONSFactoryAPI {
    /**
     * 根据自定义的属性创建一个普通的{@code Producer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties 构造{@code Producer}实例的属性
     * @return {@code Producer}实例，用于发送消息
     */
    Producer createProducer(final Properties properties);

    /**
     * 根据自定义的属性创建一个普通的{@code Consumer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties 构造{@code Consumer}实例的属性
     * @return {@code Consumer}实例，用于订阅Topic进行消息消费
     */
    Consumer createConsumer(final Properties properties);

    /**
     * 根据自定义的属性创建一个发送顺序消息的{@code OrderProducer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties 构造{@code OrderProducer}实例的属性
     * @return {@code OrderProducer}实例，用于发送顺序消息
     */
    OrderProducer createOrderProducer(final Properties properties);

    /**
     * 根据自定义的属性创建一个支持按序消费的{@code OrderConsumer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties 构造{@code OrderConsumer}实例的属性
     * @return {@code OrderConsumer}实例，用于订阅Topic进行顺序的消息消费
     */
    OrderConsumer createOrderedConsumer(final Properties properties);

    /**
     * 根据自定义的属性创建一个发送事务消息的{@code TransactionProducer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties 构造{@code TransactionProducer}实例的属性
     * @param checker    用于本地事务的状态回查，服务端会根据返回的状态决定投递或删除事务消息
     * @return {@code TransactionProducer}实例，用于发送事务消息
     */
    TransactionProducer createTransactionProducer(final Properties properties,
        final LocalTransactionChecker checker);

    /**
     * 根据自定义的属性创建一个支持主动拉取消费的{@code PullConsumer}实例，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @param properties
     * @return
     */
    PullConsumer createPullConsumer(final Properties properties);
}
