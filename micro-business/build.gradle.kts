plugins {
    id("jmicro.library-conventions")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    api(project(":micro-common"))
    api(project(":micro-auth"))

    // web stacks shared by OIDC server endpoints and pic upload
    api(project(":micro-web"))
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework:spring-webmvc")

    // Flyway support is optional: consumers must opt-in by adding the Flyway
    // dependencies themselves. compileOnly + runtimeOnly keeps them visible for
    // compilation and runtime but non-transitive (not forced onto consumers).
    compileOnly("org.springframework.boot:spring-boot-starter-flyway")
    compileOnly("org.flywaydb:flyway-core")

    // Pic upload providers (optional)
    compileOnly(libs.aliyun.oss)
    compileOnly(libs.huawei.obs)
    compileOnly(libs.minio)
    compileOnly(libs.awssdk.s3)

    testImplementation(project(":micro-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.kotlin)
}
