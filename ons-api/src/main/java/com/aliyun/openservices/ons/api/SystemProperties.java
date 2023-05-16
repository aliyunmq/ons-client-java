package com.aliyun.openservices.ons.api;

import java.io.Serializable;

public class SystemProperties implements Serializable {
    private String tag;
    private String key;
    private String msgId;
    private String shardingKey;
    private int reconsumeTimes;
    private long bornTimestamp;
    private String bornHost;
    private long startDeliverTime;
    private long partitionOffset;
    private String partition;

    public SystemProperties() {
        this.tag = "";
        this.key = "";
        this.shardingKey = "";
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(String shardingKey) {
        this.shardingKey = shardingKey;
    }

    public int getReconsumeTimes() {
        return reconsumeTimes;
    }

    public void setReconsumeTimes(int reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    public long getBornTimestamp() {
        return bornTimestamp;
    }

    public void setBornTimestamp(long bornTimestamp) {
        this.bornTimestamp = bornTimestamp;
    }

    public String getBornHost() {
        return bornHost;
    }

    public void setBornHost(String bornHost) {
        this.bornHost = bornHost;
    }

    public long getStartDeliverTime() {
        return startDeliverTime;
    }

    public void setStartDeliverTime(long startDeliverTime) {
        this.startDeliverTime = startDeliverTime;
    }

    public long getPartitionOffset() {
        return partitionOffset;
    }

    public void setPartitionOffset(long partitionOffset) {
        this.partitionOffset = partitionOffset;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getPartition() {
        return partition;
    }

    @Override
    public String toString() {
        return "SystemProperties{" +
            "tag='" + tag + '\'' +
            ", key='" + key + '\'' +
            ", msgId='" + msgId + '\'' +
            ", shardingKey='" + shardingKey + '\'' +
            ", reconsumeTimes=" + reconsumeTimes +
            ", bornTimestamp=" + bornTimestamp +
            ", bornHost='" + bornHost + '\'' +
            ", startDeliverTime=" + startDeliverTime +
            ", partitionOffset=" + partitionOffset +
            ", brokerName=" + partition +
            '}';
    }
}
