package com.jiangtj.micro.common.exceptions;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class MicroException extends RuntimeException {

    public MicroException() {
        super();
    }

    public MicroException(String message) {
        super(message);
    }

    public MicroException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
