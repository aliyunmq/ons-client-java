package com.aliyun.openservices.ons.api;

import java.util.Properties;

/**
 * 管理类接口
 */
public interface Admin {
    /**
     * 检查服务是否已经启动.
     *
     * @return <code>true</code>如果服务已启动; 其它情况返回<code>false</code>
     * @see Admin#isClosed()
     */
    boolean isStarted();

    /**
     * 检查服务是否已经关闭
     *
     * @return <code>true</code>如果服务已关闭; 其它情况返回<code>false</code>
     * @see Admin#isStarted()
     */
    boolean isClosed();

    /**
     * 启动服务
     */
    void start();

    /**
     * 更新凭据 (AccessKey/SecretKey/SecurityToken/ONSChannel)
     * 动态更新访问凭据
     *
     * @param credentialProperties STSToken相关的信息
     */
    void updateCredential(Properties credentialProperties);

    /**
     * 关闭服务
     */
    void shutdown();
}
