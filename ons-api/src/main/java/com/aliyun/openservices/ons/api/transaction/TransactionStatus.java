package com.aliyun.openservices.ons.api.transaction;

/**
 * 事务分支操作状态
 */
public enum TransactionStatus {
    /**
     * 提交事务，对应的事务消息将投递给消费者
     */
    CommitTransaction,
    /**
     * 回滚事务，对应的事务消息会被删除，不会投递给消费者
     */
    RollbackTransaction,
    /**
     * 未知状态，一般在用户无法确定事务是成功还是失败时使用，对于未知状态的事务，服务端会定期进行事务回查
     */
    Unknow
}
