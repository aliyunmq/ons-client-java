package com.aliyun.openservices.ons.api;

/**
 * 每次消费消息的上下文，供将来扩展使用
 */
public class ConsumeContext {

    private int acknowledgeIndex;

    public ConsumeContext() {
    }

    public int getAcknowledgeIndex() {
        return acknowledgeIndex;
    }

    public void setAcknowledgeIndex(int acknowledgeIndex) {
        this.acknowledgeIndex = acknowledgeIndex;
    }
}
