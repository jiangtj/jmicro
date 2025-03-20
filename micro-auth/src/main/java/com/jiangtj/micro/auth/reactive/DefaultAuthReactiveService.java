package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.core.AuthProperties;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.auth.core.AuthUtils;
import jakarta.annotation.Resource;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

public class DefaultAuthReactiveService implements AuthReactiveService {
    @Resource
    private AuthProperties properties;

    private AntPathMatcher matcher;

    @Override
    public Mono<AuthContext> getContext() {
        return AuthReactiveHolder.deferAuthContext();
    }

    @Override
    public Mono<Void> hasPermission(Logic logic, String... permissions) {
        if (properties.getPermissionMatch() == AuthProperties.MatchMode.Simple) {
            return getContext()
                .doOnNext(ctx -> AuthUtils.hasPermission(ctx, logic, permissions))
                .then();
        } else {
            if (matcher == null) {
                matcher = new AntPathMatcher(properties.getPathSeparator());
            }
            return getContext()
                .doOnNext(ctx -> AuthUtils.hasAntPermission(ctx, matcher, logic, permissions))
                .then();
        }
    }
}
