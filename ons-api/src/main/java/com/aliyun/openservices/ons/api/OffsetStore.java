package com.aliyun.openservices.ons.api;

import com.google.common.base.Optional;

public interface OffsetStore {
    /**
     * 启动方法，用户需要自己手动实现并调用，进行一些初始化操作。
     */
    void start();

    /**
     * 关闭方法，用户需要自己手动实现并调用，用于释放资源。
     */
    void shutdown();

    /**
     * 更新指定分区的位点。
     *
     * @param partition 指定的分区
     * @param offset    指定分区的位点
     */
    void updateOffset(TopicPartition partition, long offset);

    /**
     * 读取指定分区的位点，如果位点不存在，请返回 {@link Optional#absent()}
     *
     * @param partition 指定的分区
     * @return 指定分区的位点，不存在则返回 {@link Optional#absent()}
     */
    Optional<Long> readOffset(TopicPartition partition);
}
