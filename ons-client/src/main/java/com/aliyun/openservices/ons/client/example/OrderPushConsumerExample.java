package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderPushConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(OrderPushConsumerExample.class);

    private OrderPushConsumerExample() {
    }

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
        OrderConsumer consumer = ONSFactory.createOrderedConsumer(properties);

        String orderTopic = "yourOrderTopic";
        consumer.subscribe(orderTopic, "yourMessageTag", (message, context) -> {
            logger.info("Message received, topic={}, messageId={}", orderTopic, message.getMsgID());
            return OrderAction.Success;
        });
        consumer.start();
    }
}
