package com.jiangtj.micro.auth.oidc;

import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;

public interface OidcClient extends Ordered {

    String getPattern();

    MatcherStyle getMatcherStyle();

    String getPathSeparator();

    @Nullable String getJwksUri();

    @Nullable String getOpenidConfiguration() ;
}