package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import java.util.Properties;

/**
 * The {@code TransactionProducerBean} is used to integrate {@link TransactionProducer} into a Spring Bean.
 * <p> {@code TransactionProducerBean} 用于将 {@link TransactionProducer} 集成至 Spring Bean 中。
 */
public class TransactionProducerBean implements TransactionProducer {
    /**
     * 需要注入该字段，指定构造{@code TransactionProducer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see TransactionProducerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * This field needs to be injected. {@code TransactionProducer} depends on this object
     * to check the transaction status when sending transactional messages.
     *
     * @see TransactionProducerBean#setLocalTransactionChecker(LocalTransactionChecker)
     * <p>需要注入该字段，{@code TransactionProducer}在发送事务消息会依赖该对象进行事务状态回查。
     */
    private LocalTransactionChecker localTransactionChecker;

    private TransactionProducer transactionProducer;

    /**
     * Starts this {@code TransactionProducer} instance. It is recommended to configure it
     * as the init-method of the Bean.
     * <p>启动该 {@code TransactionProducer} 实例，建议配置为 Bean 的 init-method。
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        this.transactionProducer = ONSFactory.createTransactionProducer(properties, localTransactionChecker);
        this.transactionProducer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.transactionProducer != null) {
            this.transactionProducer.updateCredential(credentialProperties);
        }
    }

    /**
     * Shuts down this {@code TransactionProducer} instance. It is recommended to configure it as
     * the destroy-method of the Bean.
     * <p>关闭该 {@code TransactionProducer} 实例，建议配置为 Bean 的 destroy-method。
     */
    @Override
    public void shutdown() {
        if (this.transactionProducer != null) {
            this.transactionProducer.shutdown();
        }
    }

    @Override
    public SendResult send(Message message, LocalTransactionExecuter executer, Object arg) {
        return this.transactionProducer.send(message, executer, arg);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public LocalTransactionChecker getLocalTransactionChecker() {
        return localTransactionChecker;
    }

    public void setLocalTransactionChecker(LocalTransactionChecker localTransactionChecker) {
        this.localTransactionChecker = localTransactionChecker;
    }

    @Override
    public boolean isStarted() {
        return this.transactionProducer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.transactionProducer.isClosed();
    }
}
