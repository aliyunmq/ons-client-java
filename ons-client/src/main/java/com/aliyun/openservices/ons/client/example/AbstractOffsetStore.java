package com.aliyun.openservices.ons.client.example;

import com.aliyun.openservices.ons.api.OffsetStore;
import com.aliyun.openservices.ons.api.TopicPartition;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.java.misc.ExecutorServices;
import org.apache.rocketmq.client.java.misc.ThreadFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOffsetStore implements OffsetStore {
    private static final Logger log = LoggerFactory.getLogger(AbstractOffsetStore.class);

    private final long persistPeriodSeconds;

    private final ConcurrentMap<TopicPartition, Long> offsetTable;
    private final ScheduledExecutorService offsetPersistScheduler;

    public AbstractOffsetStore(long persistPeriodSeconds) {
        this.persistPeriodSeconds = persistPeriodSeconds;
        this.offsetTable = new ConcurrentHashMap<>();
        this.offsetPersistScheduler = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryImpl("OffsetPersistScheduler"));
    }

    @Override
    public void start() {
        final Map<TopicPartition, Long> queueOffsetTable = loadOffset();
        if (null != queueOffsetTable) {
            offsetTable.putAll(queueOffsetTable);
        }
        this.offsetPersistScheduler.scheduleWithFixedDelay(
            () -> {
                try {
                    persistOffset(offsetTable);
                } catch (Throwable t) {
                    log.error("Exception occurs while trying to persist offset", t);
                }
            },
            persistPeriodSeconds, persistPeriodSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        try {
            if (!ExecutorServices.awaitTerminated(offsetPersistScheduler)) {
                log.error("[Bug] Timeout to shutdown the offset persist scheduler.");
            }
        } catch (Throwable t) {
            log.error("Failed to shutdown the offset persist scheduler.", t);
        }
    }

    /**
     * 从磁盘或者其他外部存储读取位点。
     *
     * @return 位点存储表
     */
    public abstract Map<TopicPartition, Long> loadOffset();

    /**
     * 持久化位点到磁盘或其他外部存储介质。
     *
     * @param offsetTable 位点存储表
     */
    public abstract void persistOffset(Map<TopicPartition, Long> offsetTable);

    @Override
    public void updateOffset(TopicPartition partition, long offset) {
        offsetTable.put(partition, offset);
    }

    @Override
    public Optional<Long> readOffset(TopicPartition partition) {
        final Long offset = offsetTable.get(partition);
        if (null == offset) {
            return Optional.empty();
        }
        return Optional.of(offset);
    }
}