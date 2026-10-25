plugins {
    java
    kotlin("jvm") version "__KOTLIN_VERSION__"
    kotlin("plugin.spring") version "__KOTLIN_VERSION__"
    id("org.springframework.boot") version "__SPRING_BOOT_VERSION__"
}

group = "__GROUP__"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    // #if local-repo
    mavenLocal()
    // #endif
    mavenCentral()
}

dependencies {
    implementation(platform("com.jiangtj.micro:micro-dependencies:__JMICRO_VERSION__"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:__SPRING_BOOT_VERSION__"))

    implementation("com.jiangtj.micro:micro-auth")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // #if db|config|pic
    implementation("com.jiangtj.micro:micro-business")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // #endif
    // #if db
    implementation("com.jiangtj.micro:micro-sql-jooq")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.jooq:jooq-codegen")
    // #endif

    testImplementation("com.jiangtj.micro:micro-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

springBoot {
    mainClass.set("__PKG__.__APP_CLASS__ApplicationKt")
}

tasks.withType<Test> {
    useJUnitPlatform {
        if (project.hasProperty("ai")) {
            excludeTags("db-generate")
        }
    }
}
