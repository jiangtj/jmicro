package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
public class NoPermissionException extends BaseException {

    private final List<String> permissions;

    public NoPermissionException(List<String> permissions) {
        super(ProblemDetail.forStatus(HttpStatus.FORBIDDEN));
        this.permissions = permissions;
        ProblemDetail body = super.getBody();
        body.setTitle("No Permission");
        body.setDetail(String.format("Don't have permission<%s>.", String.join(",", permissions)));
    }

}
