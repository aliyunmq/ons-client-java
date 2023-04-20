package com.aliyun.openservices.ons.api.exception;

/**
 * 统一对外暴露的客户端异常接口
 */
public class ONSClientException extends RuntimeException {
    private static final long serialVersionUID = 5755356574640041094L;

    /**
     * 默认异常构造函数.
     */
    public ONSClientException() {
    }

    /**
     * 异常接口构造函数
     *
     * @param message 需要向外传递的异常信息
     */
    public ONSClientException(String message) {
        super(message);
    }

    /**
     * 异常接口构造函数
     *
     * @param cause 需要向外传递的异常
     */
    public ONSClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常接口构造函数
     *
     * @param message 需要向外传递的异常信息
     * @param cause   需要向外传递的异常
     */
    public ONSClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
