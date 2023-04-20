package com.aliyun.openservices.ons.api.transaction;

import com.aliyun.openservices.ons.api.Message;

/**
 * 回查本地事务，由Broker回调Producer
 */
public interface LocalTransactionChecker {
    /**
     * 回查本地事务，Broker回调Producer，将未结束的事务发给Producer，由Producer来再次决定事务是提交还是回滚
     *
     * @param msg 消息
     * @return {@link TransactionStatus} 事务状态, 包含提交事务、回滚事务、未知状态
     */
    TransactionStatus check(final Message msg);
}
