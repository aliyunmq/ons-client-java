package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(AsyncProducerExample.class);

    private AsyncProducerExample() {
    }

    public static void main(String[] args) throws InterruptedException {
        Properties properties = new Properties();
        // AccessKey ID，阿里云身份验证标识。
        properties.put(PropertyKeyConst.AccessKey, "yourAccessKey");
        // AccessKey Secret，阿里云身份验证密钥。
        properties.put(PropertyKeyConst.SecretKey, "yourSecretKey");
        // 设置发送超时时间，单位：毫秒。
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置 TCP 接入域名，进入消息队列 RocketMQ 版控制台实例详情页面的接入点区域查看。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, "yourNameSrvAddr");
        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer ，只需调用一次即可。
        producer.start();

        String topic = "yourTopic";
        String tag = "yourMessageTag";
        Message message = new Message(
            // 消息所属的 Topic ，切勿使用当前类型的 Topic 来收发其他类型的消息。
            topic,
            // Message Tag 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在消息队列 RocketMQ 版的服务器过滤。
            tag,
            // Message Body 可以是任何二进制形式的数据，消息队列 RocketMQ 版不做任何干预。
            // 需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式。
            "This is a message for RocketMQ".getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过消息队列 RocketMQ 版控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发。
        String key = "yourMessageKey";
        message.setKey(key);

        // 异步发送消息, 发送结果通过 callback 返回给客户端。
        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                final String messageId = sendResult.getMessageId();
                logger.info("Send message successfully, topic={}, messageId={}", topic, messageId);
            }

            @Override
            public void onException(OnExceptionContext context) {
                logger.error("Failed to send message, topic={}", topic, context.getException());
            }
        });

        // 阻塞当前线程3秒，等待异步发送结果。
        Thread.sleep(3000);

        // 在应用退出前，销毁Producer对象。
        // 注意：销毁Producer对象可以节约系统内存，若您需要频繁发送消息，则无需销毁Producer对象。
        producer.shutdown();
    }
}
