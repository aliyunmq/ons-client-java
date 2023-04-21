package com.aliyun.openservices.ons.api.exception;

/**
 * The unified interface for external client exceptions.
 * <p>统一对外暴露的客户端异常接口。
 */
public class ONSClientException extends RuntimeException {
    private static final long serialVersionUID = 5755356574640041094L;

    /**
     * Default exception constructor.
     * <p>默认异常构造函数。
     */
    public ONSClientException() {
    }

    /**
     * <p>Exception interface constructor.</p>
     * <p>异常接口构造函数。</p>
     *
     * @param message Message to be passed externally for the exception. / 需要向外传递的异常信息。
     */
    public ONSClientException(String message) {
        super(message);
    }

    /**
     * <p>Exception interface constructor.</p>
     * <p>异常接口构造函数。</p>
     *
     * @param cause Exception to be passed externally. / 需要向外传递的异常。
     */
    public ONSClientException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Exception interface constructor.</p>
     * <p>异常接口构造函数。</p>
     *
     * @param message Message to be passed externally for the exception. / 需要向外传递的异常信息。
     * @param cause   Exception to be passed externally. / 需要向外传递的异常。
     */
    public ONSClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
