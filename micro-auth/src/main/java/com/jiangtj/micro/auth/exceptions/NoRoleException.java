package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class NoRoleException extends BaseException {

    public NoRoleException(String role) {
        super(ProblemDetail.forStatus(HttpStatus.FORBIDDEN));
        ProblemDetail body = super.getBody();
        body.setTitle("No Role");
        body.setDetail(String.format("Don't have role<%s>.", role));
    }

}
