package com.aliyun.openservices.ons.api;

/**
 * 消费消息的返回结果
 */
public enum Action {
    /**
     * 消费成功，继续消费下一条消息
     */
    CommitMessage,
    /**
     * 消费失败，告知服务器稍后再投递这条消息，继续消费其他消息
     */
    ReconsumeLater,
}
