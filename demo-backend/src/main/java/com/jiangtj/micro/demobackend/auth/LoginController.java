package com.jiangtj.micro.demobackend.auth;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * demo 登录接口（/login 已在 WebConfig 中排除登录校验）
 */
@RestController
public class LoginController {

    @Resource
    private DemoUserStore userStore;

    @Resource
    private DemoJwtService jwtService;

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public LoginResult login(@Valid @RequestBody LoginRequest request) {
        DemoUser user = userStore.login(request.username(), request.password());
        String token = jwtService.createToken(user);
        return new LoginResult(token, user.username(), user.nickname(), user.roles(), user.permissions());
    }

    @HasLogin
    @GetMapping("/me")
    public MeResult me() {
        Subject subject = authService.getSubject();
        Authorization authorization = authService.getAuthorization();
        return new MeResult(subject.getId(), subject.getDisplayName(),
            authorization.roles(), authorization.permissions());
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResult(String token, String username, String nickname,
                              List<String> roles, List<String> permissions) {
    }

    public record MeResult(String username, String nickname,
                           List<String> roles, List<String> permissions) {
    }
}
