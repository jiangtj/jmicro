package com.jiangtj.micro.auth.exceptions;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
public class NoPermissionException extends UnAuthorizationException {

    private final List<String> permissions;

    public NoPermissionException(List<String> permissions) {
        super(String.format("Don't have permission<%s>.", String.join(",", permissions)));
        this.permissions = permissions;
        ProblemDetail body = super.getBody();
        body.setTitle("No Permission");
    }

}
