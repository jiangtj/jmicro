plugins {
    id("jmicro.library-conventions")
}

dependencies {
    api(project(":micro-common"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-json")
    api("org.springframework.boot:spring-boot-starter-kotlinx-serialization-json")
    api("org.springframework.boot:spring-boot-starter-aspectj")

    // servlet
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core")
}
