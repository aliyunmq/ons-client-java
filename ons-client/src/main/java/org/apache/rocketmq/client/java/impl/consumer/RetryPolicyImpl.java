package org.apache.rocketmq.client.java.impl.consumer;

import java.time.Duration;
import org.apache.rocketmq.client.java.retry.RetryPolicy;

public class RetryPolicyImpl implements RetryPolicy {
    private final int maxAttempts;

    public RetryPolicyImpl(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    public Duration getNextAttemptDelay(int attempt) {
        return null;
    }

    @Override
    public RetryPolicy inheritBackoff(apache.rocketmq.v2.RetryPolicy retryPolicy) {
        return null;
    }

    @Override
    public apache.rocketmq.v2.RetryPolicy toProtobuf() {
        return apache.rocketmq.v2.RetryPolicy.newBuilder().setMaxAttempts(maxAttempts).build();
    }
}
