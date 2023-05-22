package org.apache.rocketmq.client.java.impl;

import org.apache.rocketmq.client.java.retry.RetryPolicy;

public class SettingsAccessor {
    private SettingsAccessor() {
    }

    public static void setRetryPolicy(Settings settings, RetryPolicy retryPolicy) {
        settings.retryPolicy = retryPolicy;
    }
}
