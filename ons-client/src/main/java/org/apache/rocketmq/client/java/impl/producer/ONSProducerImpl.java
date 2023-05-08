package org.apache.rocketmq.client.java.impl.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.client.ClientAbstract;
import com.aliyun.openservices.ons.client.UtilAll;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import net.javacrumbs.futureconverter.java8guava.FutureConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.misc.ThreadFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class ONSProducerImpl extends ClientAbstract implements Producer, OrderProducer, TransactionProducer {
    private static final Logger log = LoggerFactory.getLogger(ONSProducerImpl.class);

    private static final long DEFAULT_SEND_MSG_TIMEOUT_MILLIS = 5 * 1000L;

    private final long sendMessageTimeoutMillis;
    private ExecutorService sendCallbackExecutor;
    private final ProducerImpl producer;

    public ONSProducerImpl(final Properties properties) {
        super(properties);
        this.producer = new ProducerImpl(clientConfiguration, new HashSet<>(), 3, null);
        final String sendMessageTimeoutMillisProp = properties.getProperty(PropertyKeyConst.SendMsgTimeoutMillis);
        this.sendMessageTimeoutMillis = StringUtils.isNoneBlank(sendMessageTimeoutMillisProp) ?
            Long.parseLong(sendMessageTimeoutMillisProp) : DEFAULT_SEND_MSG_TIMEOUT_MILLIS;
        this.sendCallbackExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryImpl("SendCallbackWorker"));
    }

    public ONSProducerImpl(final Properties properties, final LocalTransactionChecker localChecker) {
        this(properties);
        // TODO
    }

    @Override
    public void start() {
        try {
            if (this.started.compareAndSet(false, true)) {
                log.info("Begin to start the ONS producer");
                this.producer.startAsync().awaitRunning();
                log.info("ONS producer starts successfully");
                return;
            }
            log.warn("ONS producer has been started before");
        } catch (Throwable t) {
            log.error("Failed to start the ONS producer");
            throw new ONSClientException(t);
        }
    }

    @Override
    public SendResult send(Message message) {
        final CompletableFuture<SendReceipt> future0 = producer.sendAsync(UtilAll.convertMessage(message));
        final ListenableFuture<SendReceipt> future = FutureConverter.toListenableFuture(future0);
        final ScheduledExecutorService scheduler = producer.getClientManager().getScheduler();
        Futures.withTimeout(future, DEFAULT_SEND_MSG_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, scheduler);
        try {
            final SendReceipt sendReceipt = future.get(sendMessageTimeoutMillis, TimeUnit.MILLISECONDS);
            return new SendResult(message.getTopic(), sendReceipt.getMessageId().toString());
        } catch (TimeoutException | InterruptedException | ExecutionException t) {
            throw new ONSClientException(t);
        }
    }

    @Override
    public void sendOneway(Message message) {
        producer.sendAsync(UtilAll.convertMessage(message));
    }

    @Override
    public void sendAsync(Message message, SendCallback sendCallback) {
        final CompletableFuture<SendReceipt> future0 = producer.sendAsync(UtilAll.convertMessage(message));
        final ListenableFuture<SendReceipt> future = FutureConverter.toListenableFuture(future0);
        final ScheduledExecutorService scheduler = producer.getClientManager().getScheduler();
        Futures.withTimeout(future, DEFAULT_SEND_MSG_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, scheduler);
        Futures.addCallback(future, new FutureCallback<SendReceipt>() {
            @Override
            public void onSuccess(SendReceipt sendReceipt) {
                final SendResult sendResult = new SendResult(message.getTopic(), sendReceipt.getMessageId().toString());
                sendCallback.onSuccess(sendResult);
            }

            @Override
            public void onFailure(Throwable throwable) {
                final ONSClientException exception = new ONSClientException(throwable);
                final OnExceptionContext context = new OnExceptionContext(message.getTopic(),
                    message.getMsgID(), exception);
                sendCallback.onException(context);
            }
        }, sendCallbackExecutor);
    }

    @Override
    public void setCallbackExecutor(ExecutorService callbackExecutor) {
        this.sendCallbackExecutor = callbackExecutor;
    }

    @Override
    public SendResult send(Message message, String shardingKey) {
        return null;
    }

    @Override
    public SendResult send(Message message, LocalTransactionExecuter executer, Object arg) {
        return null;
    }
}
