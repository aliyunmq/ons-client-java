package com.aliyun.openservices.ons.api.order;

/**
 * <p>Enumeration of ordered message consumption results.</p>
 * <p>顺序消息消费结果枚举。</p>
 */
public enum OrderAction {
    /**
     * <p>Consumption is successful, continue consuming the next message.</p>
     * <p>消费成功，继续消费下一条消息。</p>
     */
    Success,
    /**
     * <p>Consumption failed, suspend the current queue.</p>
     * <p>消费失败，挂起当前队列。</p>
     */
    Suspend,
}