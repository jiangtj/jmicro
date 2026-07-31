// BOM-style project (Gradle java-platform), the equivalent of the Maven
// micro-dependencies pom that consumers import to align internal module versions.
plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(project(":micro-common"))
        api(project(":micro-web"))
        api(project(":micro-auth"))
        api(project(":micro-auth-oidc"))
        api(project(":micro-spring-boot-starter"))
        api(project(":micro-sql-jooq"))
        api(project(":micro-test"))
        api(project(":micro-pic-upload-starter"))
        api(project(":micro-flyway-starter"))
        api(project(":micro-payment"))
    }
}
