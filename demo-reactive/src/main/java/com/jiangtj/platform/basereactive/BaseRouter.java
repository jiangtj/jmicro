package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.web.BaseExceptionUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BaseRouter {

	@Resource
	private AuthReactiveService authReactiveService;

	@Bean
	public RouterFunction<ServerResponse> baseRoutes() {
		return route()
			.GET("/insecure/fn/err", serverRequest -> {
				throw BaseExceptionUtils.badRequest("fn");
			})
			.GET("/insecure/fn/err2", serverRequest -> {
				throw new RuntimeException("系统错误");
			})
			.GET("/fn/needtoken", serverRequest -> {
				return authReactiveService.hasLogin()
					.then(ServerResponse.ok().bodyValue("ok"));
			})
			.build();
	}
}
