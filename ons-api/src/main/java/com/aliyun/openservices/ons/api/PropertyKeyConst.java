package com.aliyun.openservices.ons.api;

import javax.annotation.Generated;

@Generated("ons-api")
public class PropertyKeyConst {

    /**
     * 消费模式，包括集群模式、广播模式
     */
    public static final String MessageModel = "MessageModel";

    /**
     * Group ID，客户端ID
     */
    public static final String GROUP_ID = "GROUP_ID";

    /**
     * AccessKey, 用于标识、校验用户身份
     */
    public static final String AccessKey = "AccessKey";

    /**
     * SecretKey, 用于标识、校验用户身份
     */
    public static final String SecretKey = "SecretKey";

    public static final String SecurityToken = "SecurityToken";

    /**
     * RAM角色授权的角色名称
     */
    public static final String RAM_ROLE_NAME = "RamRoleName";

    /**
     * 消息发送超时时间，如果服务端在配置的对应时间内未ACK，则发送客户端认为该消息发送失败。
     */
    public static final String SendMsgTimeoutMillis = "SendMsgTimeoutMillis";

    /**
     * 消息队列服务接入点
     */
    @Deprecated
    public static final String ONSAddr = "ONSAddr";

    /**
     * Name Server地址
     */
    public static final String NAMESRV_ADDR = "NAMESRV_ADDR";

    /**
     * 消费线程数量
     */
    public static final String ConsumeThreadNums = "ConsumeThreadNums";

    /**
     * 设置客户端接入来源，默认ALIYUN
     */
    @Deprecated
    public static final String OnsChannel = "OnsChannel";

    /**
     * 消息类型，可配置为NOTIFY、METAQ
     */
    public static final String MQType = "MQType";

    /**
     * 是否启动vip channel
     */
    @Deprecated
    public static final String isVipChannelEnabled = "isVipChannelEnabled";

    /**
     * 顺序消息消费失败进行重试前的等待时间 单位(毫秒)
     */
    public static final String SuspendTimeMillis = "suspendTimeMillis";

    /**
     * 消息消费失败时的最大重试次数
     */
    public static final String MaxReconsumeTimes = "maxReconsumeTimes";

    /**
     * 设置每条消息消费的最大超时时间(默认为 15 分钟),超过这个时间的消息将会被视为消费失败,等下次重新投递再次消费. 每个业务需要设置一个合理的值. 单位(分钟)
     */
    public static final String ConsumeTimeout = "consumeTimeout";

    /**
     * 设置本地批量消费聚合时间. 默认是0, 即消息从服务端取到之后立即开始消费. 该时间最大为ConsumeTimeout的一半.
     */
    public static final String BatchConsumeMaxAwaitDurationInSeconds = "batchConsumeMaxAwaitDurationInSeconds";

    /**
     * 设置事务消息的第一次回查延迟时间
     */
    public static final String CheckImmunityTimeInSeconds = "CheckImmunityTimeInSeconds";

    /**
     * 是否每次请求都带上最新的订阅关系
     */
    @Deprecated
    public static final String PostSubscriptionWhenPull = "PostSubscriptionWhenPull";

    /**
     * BatchConsumer每次批量消费的最大消息数量, 默认值为1, 允许自定义范围为[1, 1024], 实际消费数量可能小于该值.
     */
    public static final String ConsumeMessageBatchMaxSize = "ConsumeMessageBatchMaxSize";

    /**
     * Consumer允许在客户端中缓存的最大消息数量，默认值为5000，设置过大可能会引起客户端OOM，取值范围为[100, 50000]
     * <p>
     * 考虑到批量拉取，实际最大缓存量会少量超过限定值
     * <p>
     * 该限制在客户端级别生效，限定额会平均分配到订阅的Topic上，比如限制为1000条，订阅2个Topic，每个Topic将限制缓存500条
     */
    public static final String MaxCachedMessageAmount = "maxCachedMessageAmount";

    /**
     * Consumer允许在客户端中缓存的最大消息容量，默认值为512 MiB，设置过大可能会引起客户端OOM，取值范围为[16, 2048]
     * <p>
     * 考虑到批量拉取，实际最大缓存量会少量超过限定值
     * <p>
     * 该限制在客户端级别生效，限定额会平均分配到订阅的Topic上，比如限制为1000MiB，订阅2个Topic，每个Topic将限制缓存500MiB
     */
    public static final String MaxCachedMessageSizeInMiB = "maxCachedMessageSizeInMiB";

    /**
     * 设置实例名，注意：如果在一个进程中将多个Producer或者是多个Consumer设置相同的InstanceName，底层会共享连接。
     */
    @Deprecated
    public static final String InstanceName = "InstanceName";

    /**
     * MQ消息轨迹开关
     */
    public static final String MsgTraceSwitch = "MsgTraceSwitch";

    /**
     * MQ消息轨迹选择 Queue 发数据开关
     */
    @Deprecated
    public static final String MsgTraceSelectQueueEnable = "MsgTraceSelectQueueEnable";

    /**
     * Mqtt消息序列ID
     */
    public static final String MqttMessageId = "mqttMessageId";

    /**
     * Mqtt消息
     */
    public static final String MqttMessage = "mqttMessage";

    /**
     * Mqtt消息保留关键字
     */
    public static final String MqttPublishRetain = "mqttRetain";

    /**
     * Mqtt消息保留关键字
     */
    public static final String MqttPublishDubFlag = "mqttPublishDubFlag";

    /**
     * Mqtt的二级Topic，是父Topic下的子类
     */
    public static final String MqttSecondTopic = "mqttSecondTopic";

    /**
     * Mqtt协议使用的每个客户端的唯一标识
     */
    public static final String MqttClientId = "clientId";

    /**
     * Mqtt消息传输的数据可靠性级别
     */
    public static final String MqttQOS = "qoslevel";

    /**
     * 设置实例ID，充当命名空间的作用
     */
    public static final String INSTANCE_ID = "INSTANCE_ID";

    /**
     * 是否开启mqtransaction，用于使用exactly-once投递语义
     */
    @Deprecated
    public static final String EXACTLYONCE_DELIVERY = "exactlyOnceDelivery";

    /**
     * exactlyonceConsumer record manager 刷新过期记录周期
     */
    public static final String EXACTLYONCE_RM_REFRESHINTERVAL = "exactlyOnceRmRefreshInterval";

    /**
     * 每次获取最大消息数量
     */
    public static final String MAX_BATCH_MESSAGE_COUNT = "maxBatchMessageCount";

    /**
     * 顺序消费加速器开关，打开时将并发消费同一个Queue的不同Sharding Key的消息，默认关闭
     */
    @Deprecated
    public static final String ENABLE_ORDERLY_CONSUME_ACCELERATOR = "enableOrderlyConsumeAccelerator";

    /**
     * 订阅方是否是使用循环平均分配策略
     */
    @Deprecated
    public static final String ALLOCATE_MESSAGE_QUEUE_STRATEGY = "AVG_BY_CIRCLE";

    /**
     * LitePullConsumer 是否开启自动 ack 位点
     */
    public static final String AUTO_COMMIT = "autoCommit";

    /**
     * LitePullConsumer 开启自动ack位点的时间间隔，最小为1秒
     */
    public static final String AUTO_COMMIT_INTERVAL_MILLIS = "autoCommitIntervalMillis";

    /**
     * LitePullConsumer 拉取消息超时时间，单位为毫秒
     */
    public static final String POLL_TIMEOUT_MILLIS = "pollTimeoutMillis";

    private PropertyKeyConst() {
    }
}
