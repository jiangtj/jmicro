package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
public class UnAuthorizationException extends BaseException {

    public UnAuthorizationException(String msg) {
        super(ProblemDetail.forStatus(HttpStatus.FORBIDDEN));
        ProblemDetail body = super.getBody();
        body.setTitle("Not Authorization");
        body.setDetail(msg);
    }

}
