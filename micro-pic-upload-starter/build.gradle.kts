plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Pic upload providers (optional)
    compileOnly(libs.aliyun.oss)
    compileOnly(libs.huawei.obs)
    compileOnly(libs.minio)
    compileOnly(libs.awssdk.s3)

    api(project(":micro-common"))
    api(project(":micro-web"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")

    // optional web stacks
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.springframework:spring-webflux")
}
