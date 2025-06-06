package com.jiangtj.micro.common.exceptions;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class MicroProblemDetailException extends MicroHttpException {

    private final String title;
    private final String detail;

    public MicroProblemDetailException(int status, String title, String detail) {
        super(status, title + ": " + detail);
        this.title = title;
        this.detail = detail;
    }

    public MicroProblemDetailException(int status, String title, String detail, @Nullable Throwable cause) {
        super(status, title + ": " + detail, cause);
        this.title = title;
        this.detail = detail;
    }
}
