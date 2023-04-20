package com.aliyun.openservices.ons.api.bean;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * {@code ProducerBean}用于将{@link Producer}集成至Spring Bean中
 */
public class ProducerBean implements Producer {
    /**
     * 需要注入该字段，指定构造{@code Producer}实例的属性，具体支持的属性详见{@link PropertyKeyConst}
     *
     * @see ProducerBean#setProperties(Properties)
     */
    private Properties properties;
    private Producer producer;

    /**
     * 启动该{@code Producer}实例，建议配置为Bean的init-method
     */
    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        this.producer = ONSFactory.createProducer(this.properties);
        this.producer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.producer != null) {
            this.producer.updateCredential(credentialProperties);
        }
    }

    /**
     * 关闭该{@code Producer}实例，建议配置为Bean的destroy-method
     */
    @Override
    public void shutdown() {
        if (this.producer != null) {
            this.producer.shutdown();
        }
    }


    @Override
    public SendResult send(Message message) {
        return this.producer.send(message);
    }


    @Override
    public void sendOneway(Message message) {
        this.producer.sendOneway(message);
    }

    @Override
    public void sendAsync(Message message, SendCallback sendCallback) {
        this.producer.sendAsync(message, sendCallback);
    }

    @Override
    public void setCallbackExecutor(final ExecutorService callbackExecutor) {
        this.producer.setCallbackExecutor(callbackExecutor);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isStarted() {
        return this.producer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.producer.isClosed();
    }
}
