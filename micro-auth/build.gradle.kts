plugins {
    id("jmicro.library-conventions")
}

dependencies {
    api(project(":micro-web"))
    api(project(":micro-common"))
    api("org.springframework.boot:spring-boot-starter")
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    api("com.github.ben-manes.caffeine:caffeine")
    api("org.springframework.security:spring-security-crypto")

    // optional web stack
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    testImplementation(project(":micro-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.kotlin)
}
