package com.aliyun.openservices.ons.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PullConsumer extends Admin {

    interface TopicPartitionChangeListener {
        /**
         * This method will be invoked in the condition of partition numbers changed, These scenarios occur when the
         * topic is expanded or shrunk.
         *
         * @param topicPartitions partitions of topic.
         */
        void onChanged(Set<TopicPartition> topicPartitions);
    }

    /**
     * Get metadata about the partitions for a given topic. This method will issue a remote call to the server if it
     * does not already have any metadata about the given topic.
     *
     * @param topic message's topic
     * @return partitions of topic.
     */
    Set<TopicPartition> topicPartitions(String topic);

    /**
     * Manually assign a list of partitions to this consumer. This interface does not allow for incremental assignment
     * and will replace the previous assignment (if there is one).
     * <p>
     * If auto-commit is enabled, an async commit (based on the old assignment) will be triggered before the new
     * assignment replaces the old one.
     *
     * @param topicPartitions partitions of topic.
     */
    void assign(Collection<TopicPartition> topicPartitions);

    /**
     * Register a callback for sensing topic metadata changes.
     *
     * @param topic    message's topic
     * @param callback callback to receive notification when partition is changed.
     */
    void registerTopicPartitionChangedListener(String topic, TopicPartitionChangeListener callback);

    /**
     * Fetch data for the topics or partitions specified using assign API. It is an error to not have subscribed to any
     * topics or partitions before polling for data.
     *
     * @param timeout in millisecond
     * @return fetch messages of appointed topic.
     */
    List<Message> poll(long timeout);

    /**
     * Overrides the fetch offsets that the consumer will use on the next {@link #poll(long)} }. If this API is invoked
     * for the same message queue more than once, the latest offset will be used on the next poll(). Note that you may
     * lose data if this API is arbitrarily used in the middle of consumption.
     *
     * @param topicPartition partitions of topic.
     * @param offset         offset to seek.
     */
    void seek(TopicPartition topicPartition, long offset);

    /**
     * Overrides the fetch offsets with the beginning offset in server that the consumer will use on the next {@link
     * #poll(long)} }.
     *
     * @param topicPartition partitions of topic.
     */
    void seekToBeginning(TopicPartition topicPartition);

    /**
     * Overrides the fetch offsets with the end offset in server that the consumer will use on the next {@link
     * #poll(long)} }.
     *
     * @param topicPartition partitions of topic.
     */
    void seekToEnd(TopicPartition topicPartition);

    /**
     * Suspend fetching from the requested message queues. Future calls to {@link #poll(long)} will not return any
     * records from these message queues until they have been resumed using {@link #resume(Collection)}.
     * <p>
     * Note that this method does not affect message queue subscription. In particular, it does not cause a group
     * rebalance.
     *
     * @param topicPartitions partitions of topic.
     */
    void pause(Collection<TopicPartition> topicPartitions);

    /**
     * Resume specified message queues which have been paused with {@link #pause(Collection)}. New calls to {@link
     * #poll(long)} will return records from these partitions if there are any to be fetched. If the message queues were
     * not previously paused, this method is a no-op.
     *
     * @param topicPartitions partitions of topic.
     */
    void resume(Collection<TopicPartition> topicPartitions);

    /**
     * Look up the offsets for the given message queue by timestamp. The returned offset for each message queue is the
     * earliest offset whose timestamp is greater than or equal to the given timestamp in the corresponding message
     * queue.
     *
     * @param topicPartition partitions of topic.
     * @param timestamp      timestamp to look up offsets.
     * @return offset
     */
    Long offsetForTimestamp(TopicPartition topicPartition, Long timestamp);

    /**
     * Get the last committed offset for the given message queue (whether the commit happened by this process or
     * another). This offset will be used as the position for the consumer in the event of a failure.
     *
     * @param topicPartition partitions of topic.
     * @return the latest committed.
     */
    Long committed(TopicPartition topicPartition);

    /**
     * Sync commit current consumed offset to server.
     */
    void commitSync();
}
