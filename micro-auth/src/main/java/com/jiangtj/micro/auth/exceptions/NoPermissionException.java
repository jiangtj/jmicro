package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class NoPermissionException extends BaseException {

    public NoPermissionException(String permission) {
        super(ProblemDetail.forStatus(HttpStatus.FORBIDDEN));
        ProblemDetail body = super.getBody();
        body.setTitle("No Permission");
        body.setDetail(String.format("Don't have permission<%s>.", permission));
    }

}
