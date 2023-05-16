package com.aliyun.openservices.ons.api;

import java.io.Serializable;
import java.util.Properties;

/**
 * 消息类. 一条消息由主题, 消息体以及可选的消息标签, 自定义附属键值对构成.
 *
 * <p> <strong>注意:</strong> 我们对每条消息的自定义键值对的长度没有限制, 但所有的自定义键值对, 系统键值对序列化后, 所占空间不能超过32767字节. </p>
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -1385924226856188094L;

    /**
     * <p> 系统属性 </p>
     */
    final SystemProperties systemProperties;

    /**
     * <p> 消息主题, 最长不超过255个字符; 由a-z, A-Z, 0-9, 以及中划线"-"和下划线"_"构成. </p>
     *
     * <p> <strong>一条合法消息本成员变量不能为空</strong> </p>
     */
    private String topic;

    /**
     * 用户属性
     */
    private Properties userProperties;

    /**
     * <p> 消息体, 消息体长度默认不超过4M, 具体请参阅集群部署文档描述. </p>
     *
     * <p> <strong>一条合法消息本成员变量不能为空</strong> </p>
     */
    private byte[] body;

    /**
     * 默认构造函数; 必要属性后续通过Set方法设置.
     */
    @Deprecated
    public Message() {
        this(null, "", "", null);
    }

    /**
     * 消息有参构造函数
     *
     * @param topic 消息主题, 最长不超过255个字符; 由a-z, A-Z, 0-9, 以及中划线"-"和下划线"_"构成.
     * @param tag   消息标签, 合法标识符, 尽量简短且见名知意
     * @param body  消息体, 消息体长度默认不超过4M, 具体请参阅集群部署文档描述.
     */
    public Message(String topic, String tag, byte[] body) {
        this(topic, tag, "", body);
    }

    /**
     * 有参构造函数.
     *
     * @param topic 消息主题, 最长不超过255个字符; 由a-z, A-Z, 0-9, 以及中划线"-"和下划线"_"构成.
     * @param tag   消息标签, 请使用合法标识符, 尽量简短且见名知意
     * @param key   业务主键
     * @param body  消息体, 消息体长度默认不超过4M, 具体请参阅集群部署文档描述.
     */
    public Message(String topic, String tag, String key, byte[] body) {
        this.topic = topic;
        if (null != body) {
            this.body = body.clone();
        } else {
            this.body = null;
        }
        this.userProperties = new Properties();

        this.systemProperties = new SystemProperties();
        this.systemProperties.setTag(tag);
        this.systemProperties.setKey(key);
        // this.systemProperties.setBornHost(UtilAll.hostName());
        reset();
    }

    Message(String topic, byte[] body, SystemProperties systemProperties, Properties userProperties) {
        this.topic = topic;
        this.body = body;
        this.systemProperties = systemProperties;
        this.userProperties = userProperties;
    }

    /**
     * 添加用户自定义属性键值对; 该键值对在消费消费时可被获取.
     *
     * @param key   自定义键
     * @param value 对应值
     */
    public void putUserProperties(final String key, final String value) {
        if (key != null && value != null) {
            this.userProperties.put(key, value);
            reset();
        }
    }

    /**
     * 获取用户自定义键的值
     *
     * @param key 自定义键
     * @return 用户自定义键值
     */
    public String getUserProperties(final String key) {
        return (String) this.userProperties.get(key);
    }

    /**
     * 获取消息的主题
     *
     * @return 消息主题.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置消息主题.
     *
     * @param topic 消息主题
     */
    public void setTopic(String topic) {
        this.topic = topic;
        reset();
    }

    /**
     * 获取消息标签
     *
     * @return 消息标签
     */
    public String getTag() {
        return this.systemProperties.getTag();
    }

    /**
     * 设置消息标签
     *
     * @param tag 标签.
     */
    public void setTag(String tag) {
        this.systemProperties.setTag(tag);
        reset();
    }

    /**
     * 获取业务码
     *
     * @return 业务码
     */
    public String getKey() {
        return this.systemProperties.getKey();
    }

    /**
     * 设置业务码
     *
     * @param key 业务码
     */
    public void setKey(String key) {
        this.systemProperties.setKey(key);
        reset();
    }

    /**
     * 获取消息ID
     *
     * @return 该消息ID
     */
    public String getMsgID() {
        return this.systemProperties.getMsgId();
    }

    /**
     * 设置该消息ID
     *
     * @param msgId 该消息ID.
     */
    @Deprecated
    public void setMsgID(String msgId) {
    }

    public Properties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Properties userProperties) {
        this.userProperties = userProperties;
        reset();
    }

    public byte[] getBody() {
        if (null == body) {
            return null;
        }
        return body.clone();
    }

    public void setBody(byte[] body) {
        if (null == body) {
            this.body = null;
            return;
        }
        this.body = body.clone();
        reset();
    }

    /**
     * 消息消费时, 获取消息已经被重试消费的次数
     *
     * @return 重试消费次数.
     */
    public int getReconsumeTimes() {
        return this.systemProperties.getReconsumeTimes();
    }

    /**
     * 设置消息重试消费次数.
     *
     * @param reconsumeTimes 重试消费次数.
     */
    @Deprecated
    public void setReconsumeTimes(final int reconsumeTimes) {
    }

    /**
     * 获取消息的生产时间
     *
     * @return 消息的生产时间.
     */
    public long getBornTimestamp() {
        return this.systemProperties.getBornTimestamp();
    }

    /**
     * 设置消息的产生时间.
     *
     * @param bornTimestamp 消息生产时间.
     */
    @Deprecated
    public void setBornTimestamp(final long bornTimestamp) {
    }

    /**
     * 获取产生消息的主机.
     *
     * @return 产生消息的主机
     */
    public String getBornHost() {
        return this.systemProperties.getBornHost();
    }

    /**
     * 设置生产消息的主机
     *
     * @param bornHost 生产消息的主机
     */
    @Deprecated
    public void setBornHost(final String bornHost) {
    }

    /**
     * 获取定时消息开始投递时间.
     *
     * @return 定时消息的开始投递时间.
     */
    public long getStartDeliverTime() {
        return this.systemProperties.getStartDeliverTime();
    }

    public String getShardingKey() {
        return this.systemProperties.getShardingKey();
    }

    public void setShardingKey(final String shardingKey) {
        this.systemProperties.setShardingKey(shardingKey);
        reset();
    }

    /**
     * <p> 设置消息的定时投递时间（绝对时间),最大延迟时间为7天. </p> <ol> <li>延迟投递: 延迟3s投递, 设置为: System.currentTimeMillis() + 3000;</li>
     * <li>定时投递: 2016-02-01 11:30:00投递, 设置为: new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-02-01
     * 11:30:00").getTime()</li> </ol>
     */
    public void setStartDeliverTime(final long startDeliverTime) {
        this.systemProperties.setStartDeliverTime(startDeliverTime);
        reset();
    }

    /**
     * @return 该消息在所属 Partition 里的偏移量
     */
    public long getOffset() {
        return this.systemProperties.getPartitionOffset();
    }

    private void reset() {
        this.systemProperties.setBornTimestamp(System.currentTimeMillis());
        // this.systemProperties.setMsgId(MessageIdGenerator.getInstance().next());
    }

    /**
     * @return 该消息所属的 Partition
     */
    public TopicPartition getTopicPartition() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Message [topic=" + topic + ", systemProperties=" + systemProperties + ", userProperties="
            + userProperties + ", bodyLength=" + (body != null ? body.length : 0) + "]";
    }
}
