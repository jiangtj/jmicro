package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
public class NoRoleException extends BaseException {

    private final List<String> roles;

    public NoRoleException(List<String> roles) {
        super(ProblemDetail.forStatus(HttpStatus.FORBIDDEN));
        this.roles = roles;
        ProblemDetail body = super.getBody();
        body.setTitle("No Role");
        body.setDetail(String.format("Don't have role<%s>.", String.join(",", roles)));
    }

}
