package com.aliyun.openservices.ons.api;

import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import java.util.Properties;


/**
 * ONS客户端工厂类，用来创建客户端对象
 */
public class ONSFactory {

    /**
     * 工厂实现类实例. 单例模式.
     */
    private static ONSFactoryAPI onsFactory = null;

    static {
        try {
            // ons client 优先加载
            Class<?> factoryClass =
                    ONSFactory.class.getClassLoader().loadClass(
                            "com.aliyun.openservices.ons.api.impl.ONSFactoryNotifyAndMetaQImpl");
            onsFactory = (ONSFactoryAPI) factoryClass.newInstance();
        } catch (Throwable e) {
            try {
                Class<?> factoryClass =
                        ONSFactory.class.getClassLoader().loadClass(
                                "com.aliyun.openservices.ons.client.ONSFactoryImpl");
                onsFactory = (ONSFactoryAPI) factoryClass.newInstance();
            } catch (Throwable e1) {
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
    }

    private ONSFactory() {
    }

    /**
     * 创建Producer
     *
     * <p><code>properties</code>
     * 应该至少包含以下几项必选配置内容:
     *     <ol>
     *         <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *         <li>{@link PropertyKeyConst#AccessKey}</li>
     *         <li>{@link PropertyKeyConst#SecretKey}</li>
     *         <li>{@link PropertyKeyConst#ONSAddr}</li>
     *     </ol>
     *     以下为可选内容
     *     <ol>
     *         <li>{@link PropertyKeyConst#OnsChannel}</li>
     *         <li>{@link PropertyKeyConst#SendMsgTimeoutMillis}</li>
     *         <li>{@link PropertyKeyConst#NAMESRV_ADDR} 该属性会覆盖{@link PropertyKeyConst#ONSAddr}</li>
     *     </ol>
     *
     *
     * <p>
     *     返回创建的{@link Producer}实例是线程安全, 可复用, 发送各个主题. 一般情况下, 一个进程中构建一个实例足够满足发送消息的需求.
     *
     * <p>
     *   示例代码:
     *   <pre>
     *        Properties props = ...;
     *        // 设置必要的属性
     *        Producer producer = ONSFactory.createProducer(props);
     *        producer.start();
     *
     *        //producer之后可以当成单例使用
     *
     *        // 发送消息
     *        Message msg = ...;
     *        SendResult result = produer.send(msg);
     *
     *        // 应用程序关闭退出时
     *        producer.shutdown();
     *   </pre>
     *
     * @param properties Producer的配置参数
     * @return {@link Producer} 实例
     */
    public static Producer createProducer(final Properties properties) {
        return onsFactory.createProducer(properties);
    }


    /**
     * 创建顺序Producer
     * <p>
     * <code>properties</code>应该至少包含以下几项配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#NAMESRV_ADDR}</li>
     *     <li>{@link PropertyKeyConst#OnsChannel}</li>
     *     <li>{@link PropertyKeyConst#SendMsgTimeoutMillis}</li>
     * </ul>
     *
     * @param properties Producer的配置参数
     * @return {@code OrderProducer} 实例
     */
    public static OrderProducer createOrderProducer(final Properties properties) {
        return onsFactory.createOrderProducer(properties);
    }


    /**
     * 创建事务Producer
     * <p>
     * <code>properties</code>应该至少包含以下几项配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#NAMESRV_ADDR}</li>
     *     <li>{@link PropertyKeyConst#OnsChannel}</li>
     *     <li>{@link PropertyKeyConst#SendMsgTimeoutMillis}</li>
     *     <li>{@link PropertyKeyConst#CheckImmunityTimeInSeconds}</li>
     * </ul>
     *
     * @param properties Producer的配置参数
     * @param checker    本地事务检查器
     * @return {@code TransactionProducer} 实例
     */
    public static TransactionProducer createTransactionProducer(final Properties properties,
                                                                final LocalTransactionChecker checker) {
        return onsFactory.createTransactionProducer(properties, checker);
    }


    /**
     * 创建Consumer
     * <p>
     * <code>properties</code>应该至少包含以下几项配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#ConsumeThreadNums}</li>
     *     <li>{@link PropertyKeyConst#ConsumeTimeout}</li>
     *     <li>{@link PropertyKeyConst#OnsChannel}</li>
     * </ul>
     *
     * @param properties Consumer的配置参数
     * @return {@code Consumer} 实例
     */
    public static Consumer createConsumer(final Properties properties) {
        return onsFactory.createConsumer(properties);
    }

    /**
     * 创建BatchConsumer
     * <p>
     * <code>properties</code>应该至少包含以下几项配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#ConsumeThreadNums}</li>
     *     <li>{@link PropertyKeyConst#ConsumeTimeout}</li>
     *     <li>{@link PropertyKeyConst#ConsumeMessageBatchMaxSize}</li>
     *     <li>{@link PropertyKeyConst#OnsChannel}</li>
     * </ul>
     *
     * @param properties BatchConsumer的配置参数
     * @return {@code BatchConsumer} 实例
     */
    public static BatchConsumer createBatchConsumer(final Properties properties) {
        return onsFactory.createBatchConsumer(properties);
    }


    /**
     * 创建顺序Consumer
     * <p>
     * <code>properties</code>应该至少包含以下几项必须配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#ConsumeThreadNums}</li>
     *     <li>{@link PropertyKeyConst#ConsumeTimeout}</li>
     *     <li>{@link PropertyKeyConst#OnsChannel}</li>
     * </ul>
     *
     * @param properties Consumer的配置参数
     * @return {@code OrderConsumer} 实例
     */
    public static OrderConsumer createOrderedConsumer(final Properties properties) {
        return onsFactory.createOrderedConsumer(properties);
    }

    /**
     * 创建主动拉取的Consumer
     * <p>
     * <code>properties</code>应该至少包含以下几项必须配置内容:
     * <ol>
     *     <li>{@link PropertyKeyConst#GROUP_ID}</li>
     *     <li>{@link PropertyKeyConst#AccessKey}</li>
     *     <li>{@link PropertyKeyConst#SecretKey}</li>
     *     <li>{@link PropertyKeyConst#ONSAddr}</li>
     * </ol>
     * 以下为可选配置项:
     * <ul>
     *     <li>{@link PropertyKeyConst#AUTO_COMMIT}</li>
     * </ul>
     *
     * @param properties Consumer的配置参数
     * @return {@code PullConsumer} 实例
     */
    public static PullConsumer createPullConsumer(final Properties properties) {
        return onsFactory.createPullConsumer(properties);
    }
}
