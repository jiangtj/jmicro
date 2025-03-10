package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UnLoginException extends BaseException {

    public UnLoginException() {
        super(ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED));
        ProblemDetail body = super.getBody();
        body.setTitle("No Login");
        body.setDetail("You have to take a token for this request.");
    }

}
