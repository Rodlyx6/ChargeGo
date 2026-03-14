package com.example.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中的可预期异常（如参数错误、状态不符等）
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
