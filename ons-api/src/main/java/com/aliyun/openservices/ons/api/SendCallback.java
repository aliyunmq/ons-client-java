package com.aliyun.openservices.ons.api;

/**
 * 异步发送完成后, 回调接口.
 */
public interface SendCallback {

    /**
     * 发送成功回调的方法.
     *
     * @param sendResult 发送结果
     */
    void onSuccess(final SendResult sendResult);

    /**
     * 发送失败回调方法.
     *
     * @param context 失败上下文.
     */
    void onException(final OnExceptionContext context);
}
