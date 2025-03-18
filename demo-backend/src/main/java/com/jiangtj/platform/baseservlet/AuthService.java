package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.core.AuthUtils;
import com.jiangtj.micro.auth.servlet.SimpleAuthService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends SimpleAuthService {

    @Override
    public void hasPermission(@NonNull Logic logic, @NonNull String... permissions) {
        AuthUtils.hasAntPermission(getContext(), logic, permissions);
    }

}
