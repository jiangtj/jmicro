plugins {
    id("jmicro.library-conventions")
}

dependencies {
    api(project(":micro-common"))
    api("org.springframework.boot:spring-boot-starter-jooq")
    api("org.springframework.boot:spring-boot-data-commons")

    compileOnly("org.jooq:jooq-meta")
    compileOnly("org.jooq:jooq-codegen")

    testImplementation("org.jooq:jooq-codegen")
    // 代码生成测试使用的内存数据库（建表脚本见 src/test/resources/test.sql）
    testImplementation("com.h2database:h2")
    testImplementation("jakarta.validation:jakarta.validation-api")
}
