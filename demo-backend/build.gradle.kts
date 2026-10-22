plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":micro-spring-boot-starter"))
    implementation(project(":micro-auth"))
    implementation(project(":micro-business"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation(project(":micro-test"))
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
}

// Demo apps should not be published.
tasks.named<Jar>("jar") {
    enabled = false
}
