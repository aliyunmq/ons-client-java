package com.aliyun.openservices.ons.api;

public class TopicPartition {
    private String topic;

    private String partition;

    public TopicPartition(String topic, String partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result + ((partition == null) ? 0 : partition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TopicPartition other = (TopicPartition) obj;
        if (partition == null) {
            if (other.partition != null) {
                return false;
            }
        } else if (!partition.equals(other.partition)) {
            return false;
        }
        if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopicPartition{" +
               "topic='" + topic + '\'' +
               ", partition='" + partition + '\'' +
               '}';
    }
}
