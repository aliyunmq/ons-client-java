package com.aliyun.openservices.ons.api.transaction;

import com.aliyun.openservices.ons.api.Message;

/**
 * 本地事务执行器
 */
public interface LocalTransactionExecuter {
    /**
     * 执行本地事务，由应用来重写
     *
     * @param msg 消息
     * @param arg 应用自定义参数，由send方法传入并回调
     * @return {@link TransactionStatus} 返回事务执行结果，包括提交事务、回滚事务、未知状态
     */
    TransactionStatus execute(final Message msg, final Object arg);
}
