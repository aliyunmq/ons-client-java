package org.apache.rocketmq.client.java.impl.consumer;

import com.aliyun.openservices.ons.client.ClientAbstract;
import java.util.Properties;

public class PushConsumer extends ClientAbstract {
    public PushConsumer(Properties properties) {
        super(properties);
    }
}
