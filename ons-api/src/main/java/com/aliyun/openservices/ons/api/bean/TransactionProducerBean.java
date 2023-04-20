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
 * {@code TransactionProducerBean}用于将{@link TransactionProducer}集成至Spring Bean中
 */
public class TransactionProducerBean implements TransactionProducer {
    /**
     * 需要注入该字段，指定构造{@code TransactionProducer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see TransactionProducerBean#setProperties(Properties)
     */
    private Properties properties;

    /**
     * 需要注入该字段，{@code TransactionProducer}在发送事务消息会依赖该对象进行事务状态回查
     *
     * @see TransactionProducerBean#setLocalTransactionChecker(LocalTransactionChecker)
     */
    private LocalTransactionChecker localTransactionChecker;

    private TransactionProducer transactionProducer;

    /**
     * 启动该{@code TransactionProducer}实例，建议配置为Bean的init-method
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
     * 关闭该{@code TransactionProducer}实例，建议配置为Bean的destroy-method
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
