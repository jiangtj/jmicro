plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

dependencies {
    api(project(":micro-web"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")
    api("org.hibernate.validator:hibernate-validator")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("com.github.ben-manes.caffeine:caffeine")

    // optional web stacks
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core")
    compileOnly("org.springframework:spring-webflux")
    compileOnly("org.springframework:spring-webmvc")

    // Maven <optional>true</optional> deps are available on the test classpath;
    // mirror that for tests that use reactor types via spring-webflux.
    testImplementation("org.springframework:spring-webflux")
}
