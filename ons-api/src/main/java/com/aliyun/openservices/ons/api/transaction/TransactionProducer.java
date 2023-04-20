package com.aliyun.openservices.ons.api.transaction;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;

/**
 * 发送分布式事务消息
 */
public interface TransactionProducer extends Admin {
    /**
     * 启动事务消息发送Producer服务
     */
    @Override
    void start();

    /**
     * 关闭事务消息发送Producer服务
     */
    @Override
    void shutdown();

    /**
     * 该方法用来发送一条事务型消息. 一条事务型消息发送分为三个步骤:
     * <ol>
     *     <li>本服务实现类首先发送一条半消息到到消息服务器;</li>
     *     <li>通过<code>executer</code>执行本地事务;</li>
     *     <li>根据上一步骤执行结果, 决定发送提交或者回滚第一步发送的半消息;</li>
     * </ol>
     *
     * @param message  要发送的事务型消息
     * @param executer 本地事务执行器
     * @param arg      应用自定义参数，该参数可以传入本地事务执行器
     * @return 发送结果.
     */
    SendResult send(final Message message,
        final LocalTransactionExecuter executer,
        final Object arg);
}
