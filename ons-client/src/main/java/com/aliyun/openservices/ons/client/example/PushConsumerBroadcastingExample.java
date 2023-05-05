package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushConsumerBroadcastingExample {
    private static final Logger logger = LoggerFactory.getLogger(PushConsumerBroadcastingExample.class);

    private PushConsumerBroadcastingExample() {
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        // 您在消息队列 RocketMQ 版控制台创建的 Group ID 。
        properties.put(PropertyKeyConst.GROUP_ID, "yourGroupId");
        // AccessKey ID，阿里云身份验证标识。
        properties.put(PropertyKeyConst.AccessKey, "yourAccessKey");
        // AccessKey Secret，阿里云身份验证密钥。
        properties.put(PropertyKeyConst.SecretKey, "yourSecretKey");
        // 广播订阅方式设置。
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
        // 设置发送超时时间，单位：毫秒。
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置 TCP 接入域名，进入消息队列 RocketMQ 版控制台实例详情页面的接入点区域查看。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, "yourNameSrvAddr");
        Consumer consumer = ONSFactory.createConsumer(properties);
        // 订阅第一个 Topic 。
        String topicA = "yourTopicA";
        // 订阅多个 Tag 。
        consumer.subscribe(topicA, "TagA0||TagA1", (message, context) -> {
            logger.info("Message received, topic={}, messageId={}", topicA, message.getMsgID());
            return Action.CommitMessage;
        });

        // 订阅另外一个 Topic 。
        String topicB = "yourTopicB";
        consumer.subscribe(topicB, "tagB0||tagB1", (message, context) -> {
            logger.info("Message received, topic={}, messageId={}", topicB, message.getMsgID());
            return Action.CommitMessage;
        });

        consumer.start();
    }
}
