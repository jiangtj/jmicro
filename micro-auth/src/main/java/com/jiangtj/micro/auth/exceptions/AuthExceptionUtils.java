package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import com.jiangtj.micro.web.BaseExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class AuthExceptionUtils {

    /**
     * 401 未登录
     */
    public static BaseException unLogin() {
        return new UnLoginException();
    }

    /**
     * 403 无权限
     */
    public static BaseException invalidToken(String msg, @Nullable Throwable cause) {
        BaseException exception = BaseExceptionUtils.from(HttpStatus.FORBIDDEN, msg, cause);
        exception.setTitle("Invalid Token");
        return exception;
    }

    /**
     * 403 Un Support
     */
    public static BaseException unSupport() {
        BaseException exception = BaseExceptionUtils.forbidden("Don't support this operate with current token.");
        exception.setTitle("Un Support");
        return exception;
    }

    /**
     * 403 No Permission
     */
    public static BaseException noRole(String role) {
        BaseException exception = BaseExceptionUtils.forbidden(String.format("Don't have role<%s>.", role));
        exception.setTitle("No Role");
        return exception;
    }

    /**
     * 403 No Permission
     */
    public static BaseException noPermission(String permission) {
        BaseException exception = BaseExceptionUtils.forbidden(String.format("Don't have permission<%s>.", permission));
        exception.setTitle("No Permission");
        return exception;
    }

}
