// BOM-style project (Gradle java-platform), the equivalent of the Maven
// micro-dependencies pom that consumers import to align internal module versions.
plugins {
    `java-platform`
    id("jmicro.publish-conventions")
}

dependencies {
    constraints {
        api(project(":micro-common"))
        api(project(":micro-web"))
        api(project(":micro-auth"))
        api(project(":micro-sql-jooq"))
        api(project(":micro-test"))
        api(project(":micro-business"))
    }
}
