package com.jiangtj.micro.common.exceptions;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class MicroHttpException extends MicroException {

    private final int status;

    public MicroHttpException(int status) {
        super("status: " + status);
        this.status = status;
    }

    public MicroHttpException(int status, String message) {
        super(message + "(" + status + ")");
        this.status = status;
    }

    public MicroHttpException(int status, String message, @Nullable Throwable cause) {
        super(message + "(" + status + ")", cause);
        this.status = status;
    }
}
