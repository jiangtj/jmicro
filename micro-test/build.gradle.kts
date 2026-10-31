plugins {
    id("jmicro.library-conventions")
}

dependencies {
    api(project(":micro-common"))
    api(project(":micro-auth"))
    api("org.springframework.boot:spring-boot-starter-test")

    compileOnly("org.springframework.boot:spring-boot-webmvc-test")
    compileOnly("org.springframework.boot:spring-boot-starter-restclient-test")
}
