package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import java.util.Date;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerFifoMessageExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerFifoMessageExample.class);

    private ProducerFifoMessageExample() {
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        // AccessKey ID，阿里云身份验证标识。获取方式，请参见创建AccessKey。
        properties.put(PropertyKeyConst.AccessKey, "yourAccessKey");
        // AccessKey Secret，阿里云身份验证密钥。获取方式，请参见创建AccessKey。
        properties.put(PropertyKeyConst.SecretKey, "yourSecretKey");
        // 设置发送超时时间，单位：毫秒。
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置TCP接入域名，进入消息队列RocketMQ版控制台实例详情页面的接入点区域查看。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, "yourNameSrvAddr");
        OrderProducer producer = ONSFactory.createOrderProducer(properties);
        // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
        producer.start();

        // 循环发送消息。
        for (int i = 0; i < 100; i++) {
            Message msg = new Message(
                // 普通消息所属的Topic，切勿使用普通消息的Topic来收发其他类型的消息。
                "yourFifoTopic",
                // Message Tag可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在消息队列RocketMQ版的服务器过滤。
                // Tag的具体格式和设置方法，请参见Topic与Tag最佳实践。
                "TagA",
                // Message Body可以是任何二进制形式的数据，消息队列RocketMQ版不做任何干预。
                // 需要Producer与Consumer协商好一致的序列化和反序列化方式。
                "This is a FIFO message for ONS".getBytes());
            // 设置代表消息的业务关键属性，请尽可能全局唯一。
            // 以方便您在无法正常收到消息情况下，可通过消息队列RocketMQ版控制台查询消息并补发。
            // 注意：不设置也不会影响消息正常收发。
            msg.setKey("ORDERID_" + i);
            // 分区顺序消息中区分不同分区的关键字段，Sharding Key与普通消息的key是完全不同的概念。
            // 全局顺序消息，该字段可以设置为任意非空字符串。
            String shardingKey = "yourShardingKey";
            try {
                SendResult sendResult = producer.send(msg, shardingKey);
                // 同步发送消息，只要不抛异常就是成功。
                if (sendResult != null) {
                    logger.info("Send FIFO message successfully, topic={}, messageId={}", msg.getTopic(), sendResult.getMessageId());
                }
            } catch (Exception e) {
                // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理。
                logger.error("Failed to send FIFO message, topic={}", msg.getTopic(), e);
            }
        }

        // 在应用退出前，销毁Producer对象。
        // 注意：销毁Producer对象可以节约系统内存，若您需要频繁发送消息，则无需销毁Producer对象。
        producer.shutdown();
    }
}
