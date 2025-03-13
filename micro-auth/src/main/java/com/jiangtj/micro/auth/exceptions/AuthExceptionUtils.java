package com.jiangtj.micro.auth.exceptions;

import com.jiangtj.micro.web.BaseException;
import com.jiangtj.micro.web.BaseExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

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
     * 403 Un Support
     */
    public static BaseException unSupport() {
        BaseException exception = BaseExceptionUtils.forbidden("Don't support this operate with current token.");
        exception.setTitle("Un Support");
        return exception;
    }

    /**
     * 403 No Role
     */
    public static NoRoleException noRole(String role) {
        return new NoRoleException(role);
    }

    /**
     * 403 No Permission
     */
    public static NoPermissionException noPermission(String permission) {
        return new NoPermissionException(permission);
    }

}
