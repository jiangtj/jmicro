package com.jiangtj.micro.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProblemDetailMvcConsumer {

    private final ProblemDetail detail;

    public static ProblemDetailMvcConsumer unLogin() {
        return new ProblemDetailMvcConsumer(HttpStatus.UNAUTHORIZED)
                .title("No Login")
                .detail("You have to take a token for this request.");
    }

    // {"type":"about:blank","title":"Invalid Token","status":403,"detail":"不支持的
    // Auth Context","instance":"/actuator"}
    public static ProblemDetailMvcConsumer unInvalid(String msg) {
        return new ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("Invalid Token")
                .detail(msg);
    }

    // {"type":"about:blank","title":"No Role","status":403,"detail":"Don't have
    // role<roletest2>.","instance":"/role-test-2"}
    public static ProblemDetailMvcConsumer unRole(String role) {
        return new ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("No Role")
                .detail(String.format("Don't have role<%s>.", role));
    }

    // {"type":"about:blank","title":"No Permission","status":403,"detail":"Don't
    // have permission<system:user:write>","instance":"/user"}
    public static ProblemDetailMvcConsumer unPermission(String permissionName) {
        return new ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("No Permission")
                .detail(String.format("Don't have permission<%s>.", permissionName));
    }

    public static ProblemDetailMvcConsumer forStatus(HttpStatus status) {
        return new ProblemDetailMvcConsumer(status)
                .title(status.getReasonPhrase());
    }

    ProblemDetailMvcConsumer(HttpStatusCode code) {
        detail = ProblemDetail.forStatus(code);
    }

    public ProblemDetailMvcConsumer title(String title) {
        this.detail.setTitle(title);
        return this;
    }

    public ProblemDetailMvcConsumer detail(String detail) {
        this.detail.setDetail(detail);
        return this;
    }

    public RestTestClient.ResponseSpec.ResponseSpecConsumer expect() {
        return responseSpec -> {

            responseSpec.expectStatus().is4xxClientError();
            responseSpec.expectStatus().isEqualTo(detail.getStatus());
            RestTestClient.BodyContentSpec expectBody = responseSpec.expectBody();
            expectBody
                .jsonPath("status").isEqualTo(detail.getStatus())
                .jsonPath("instance").exists();
            String title = detail.getTitle();
            if (title != null) {
                expectBody.jsonPath("title").isEqualTo(title);
            }
            String detailS = detail.getDetail();
            if (detailS != null) {
                expectBody.jsonPath("detail").isEqualTo(detailS);
            }
        };
    }

    // {"type":"about:blank","title":"Validation
    // failure","status":400,"detail":"手机号格式不正确"}
    public static RestTestClient.ResponseSpec.ResponseSpecConsumer unValidation(String... failures) {
        return responseSpec -> {
            responseSpec.expectStatus().is4xxClientError();
            responseSpec.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
            responseSpec.expectBody()
                    .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                    .jsonPath("title").isEqualTo("Validation failure")
                    .jsonPath("detail").value(String.class, d -> {
                        for (String failure : failures) {
                            assertTrue(d.contains(failure));
                        }
                    })
                    .jsonPath("instance").exists();
        };
    }

}
