package com.aliyun.openservices.ons.api;

import java.util.concurrent.ExecutorService;

/**
 * 消息生产者接口.
 * <p>
 *     <ol>
 *         <li>
 *              <strong>线程安全性:</strong> 本接口的实现类都是线程安全的, 可以多线程并发发送消息.
 *         </li>
 *         <li>一个实例可以发送多个主题的消息</li>
 *         <li>正常情况下, 一个实例足够高效完成本模块的发送任务, 无需创建多个实例</li>
 *     </ol>
 */
public interface Producer extends Admin {

    /**
     * 启动服务
     */
    @Override
    void start();

    /**
     * 关闭服务
     */
    @Override
    void shutdown();

    /**
     * 同步发送消息，只要不抛异常就表示成功
     *
     * @param message 要发送的消息对象
     * @return 发送结果，含消息Id, 消息主题
     */
    SendResult send(final Message message);

    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param message 要发送的消息
     */
    void sendOneway(final Message message);

    /**
     * 发送消息，异步Callback形式
     *
     * @param message      要发送的消息
     * @param sendCallback 发送完成要执行的回调函数
     */
    void sendAsync(final Message message, final SendCallback sendCallback);

    /**
     * 设置异步发送消息执行Callback的目标线程池。
     * <p>
     * 如果不设置，将使用公共线程池，仅建议执行轻量级的Callback任务，避免阻塞公共线程池
     * 引起其它链路超时。
     *
     * @param callbackExecutor 执行Callback的线程池
     */
    void setCallbackExecutor(final ExecutorService callbackExecutor);
}
