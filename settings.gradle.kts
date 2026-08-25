pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://maven.pkg.github.com/jiangtj/jmicro") }
    }
}

rootProject.name = "jmicro"

include(
    // lib
    "micro-common",
    "micro-web",
    "micro-auth",
    "micro-spring-boot-starter",
    "micro-sql-jooq",
    "micro-test",
    "micro-dependencies",
    "micro-pic-upload-starter",
    "micro-business",
    // server (demo apps)
    "demo-backend",
    "demo-reactive",
)
