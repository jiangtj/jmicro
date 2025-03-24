package com.jiangtj.micro.auth.exceptions;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
public class NoRoleException extends UnAuthorizationException {

    private final List<String> roles;

    public NoRoleException(List<String> roles) {
        super(String.format("Don't have role<%s>.", String.join(",", roles)));
        this.roles = roles;
        ProblemDetail body = super.getBody();
        body.setTitle("No Role");
    }

}
