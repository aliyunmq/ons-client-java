package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PullConsumer;
import com.aliyun.openservices.ons.api.TopicPartition;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(PullConsumerExample.class);

    private PullConsumerExample() {
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        Properties properties = new Properties();
        // 您在消息队列 RocketMQ 版控制台创建的 Group ID 。
        properties.put(PropertyKeyConst.GROUP_ID, "yourGroupId");
        // AccessKey ID，阿里云身份验证标识。
        properties.put(PropertyKeyConst.AccessKey, "yourAccessKey");
        // AccessKey Secret，阿里云身份验证密钥。
        properties.put(PropertyKeyConst.SecretKey, "yourSecretKey");
        // 设置发送超时时间，单位：毫秒。
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
        // 设置 TCP 接入域名，进入消息队列 RocketMQ 版控制台实例详情页面的接入点区域查看。
        properties.put(PropertyKeyConst.NAMESRV_ADDR, "yourNameSrvAddr");
        final PullConsumer consumer = ONSFactory.createPullConsumer(properties);
        consumer.start();
        String topic = "yourTopic";
        final Set<TopicPartition> partitions = consumer.topicPartitions(topic);
        consumer.assign(partitions);
        for (TopicPartition partition : partitions) {
            consumer.seekToBeginning(partition);
        }
        while (true) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            final List<Message> messages = consumer.poll(5000);
            final long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info("Pulled {} messages, costTime={}ms", messages.size(), elapsed);
            for (Message message : messages) {
                logger.info("Message is pulled, messageId={}", message.getMsgID());
            }
        }
    }
}
