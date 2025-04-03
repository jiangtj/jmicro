package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import com.jiangtj.micro.web.BaseExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.List;

public class AuthExceptionUtils {

    /**
     * 401 未登录
     */
    public static UnLoginException unLogin() {
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
     * 403 Forbidden
     */
    public static BaseException forbidden() {
        BaseException exception = BaseExceptionUtils.forbidden("Don't support this operate with current token.");
        exception.setTitle("Forbidden");
        return exception;
    }

    /**
     * 403 Un Authorization
     */
    public static BaseException unAuthorization(String msg) {
        return new UnAuthorizationException(msg);
    }

    /**
     * 403 No Role
     */
    public static NoRoleException noRole(List<String> roles) {
        return new NoRoleException(roles);
    }

    /**
     * 403 No Permission
     */
    public static NoPermissionException noPermission(List<String> permissions) {
        return new NoPermissionException(permissions);
    }

}
