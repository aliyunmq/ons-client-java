package com.aliyun.openservices.ons.api.order;

/**
 * 顺序消息消费结果
 */
public enum OrderAction {
    /**
     * 消费成功，继续消费下一条消息
     */
    Success,
    /**
     * 消费失败，挂起当前队列
     */
    Suspend,
}
