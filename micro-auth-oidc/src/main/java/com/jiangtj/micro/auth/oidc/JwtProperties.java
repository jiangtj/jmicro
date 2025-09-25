package com.jiangtj.micro.auth.oidc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jmicro.jwt")
public class JwtProperties {
    private List<OidcProperties> oidc = new ArrayList<>();
}