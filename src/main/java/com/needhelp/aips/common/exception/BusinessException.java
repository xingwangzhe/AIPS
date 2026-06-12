package com.needhelp.aips.common.exception;

/**
 * 业务异常类。
 * 用于在 Service 层抛出，由 GlobalExceptionHandler 统一捕获处理。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public int getCode() {
        return code;
    }
}
