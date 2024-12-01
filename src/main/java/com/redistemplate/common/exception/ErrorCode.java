package com.redistemplate.common.exception;

import lombok.*;

@AllArgsConstructor
@Getter
public enum ErrorCode implements Interface {
    REDIS_VALUE_NOT_FOUND(100, "redis value not found");

    private final Integer code;
    private final String message;
}
