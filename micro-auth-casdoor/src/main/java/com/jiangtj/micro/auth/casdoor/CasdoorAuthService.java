package com.jiangtj.micro.auth.casdoor;

import org.casbin.casdoor.config.Config;
import org.casbin.casdoor.service.AuthService;

public class CasdoorAuthService extends AuthService {
    public CasdoorAuthService(Config config) {
        super(config);
    }
}
