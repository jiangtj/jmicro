# AGENTS Guide for `jmicro`

## Big Picture (Monorepo + Module Boundaries)
- This repo is a Spring Boot 4 multi-module monorepo built with Gradle (Kotlin DSL: `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`). `micro-dependencies` is a `java-platform` project (Gradle BOM equivalent).
- Core library modules live under `micro-*`; the runnable demo is `demo-backend` (Servlet, port `17001`, `demo-backend/src/main/resources/application.properties`).
- Dependency version alignment is centralized in the `micro-dependencies` java-platform project and consumed by other modules; the Spring Boot BOM is imported via the `io.spring.dependency-management` plugin configured in the root `build.gradle.kts`.
- Feature starters are self-contained: `micro-auth` (auth/OIDC) and `micro-business` (Flyway support + optional OIDC server + picture upload, package `com.jiangtj.micro.business.pic`) wire their own defaults via auto-configuration.
- OIDC/JWT 核心能力（包 `com.jiangtj.micro.auth.oidc`）位于 `micro-auth` 模块；可选的 OIDC Server（Cas）能力位于 `micro-business` 模块（包 `com.jiangtj.micro.business.oidc.cas`，默认关闭），并依赖 `micro-auth` 提供的 OIDC 基础能力。

## Architecture Patterns You Should Follow
- Prefer Spring Boot auto-configuration extension points over direct wiring: each starter registers via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- Auth is intentionally lightweight and filter/AOP-centered (not Spring Security-first): see `micro-auth/README.md` and `micro-auth/src/main/java/com/jiangtj/micro/auth/AuthAutoConfiguration.java`.
- The library targets the Servlet stack only (Spring MVC); WebFlux/reactive support is intentionally out of scope.
- Shared JSON behavior is initialized in auto-config (`micro-web/src/main/java/com/jiangtj/micro/web/JMicroCommonAutoConfiguration.java` -> `JsonUtils.init(mapper)`).

## Config and Integration Hotspots
- Property namespaces are module-specific and important: `jmicro.auth.*`, `jmicro.jwt.*`, `jmicro.flyway.*`, `jmicro.pic.upload.*` (picture upload, lives in `micro-business`), plus provider namespaces (`minio.*`, `hw.obs.*`, `easyimages.api.*`). Ali OSS 配置已改为系统配置项（键 `ali.oss.*`，需 `jmicro.system.config.enabled=true`，见 `micro-business/.../pic/upload/ali/AliOSSConfigLoader.kt`）。
- OIDC/JWT support is in `micro-auth` (package `com.jiangtj.micro.auth.oidc`); the optional OIDC server (Cas) support is in `micro-business` (package `com.jiangtj.micro.business.oidc.cas`) and is opt-in.
- Demo auth integration depends on Casdoor (`docker-compose.yml` and root `README.md` Casdoor setup).

## Developer Workflows (Project-Specific)
- Local library install (recommended by maintainers, publishes to Maven Local):
  - `./gradlew publishToMavenLocal`
- Build/test all modules from root:
  - `./gradlew test`
- Run the demo backend:
  - `./gradlew :demo-backend:bootRun`

## Testing and Coding Conventions Seen in Repo
- Integration tests commonly use `@JMicroTest` from `micro-test` (`micro-test/src/main/java/com/jiangtj/micro/test/JMicroTest.java`) and auth helpers like `@WithMockUser` (see `micro-auth/README.md`).
- Nullness is explicit via JSpecify `@NullMarked` package-level defaults across modules (search `**/package-info.java`).
- Java + Kotlin coexist in modules; keep interop-friendly APIs and avoid introducing framework patterns that bypass the existing auto-configuration design.
- When adding new starter behavior, mirror existing pattern: properties class + auto-config + `AutoConfiguration.imports` registration + demo/test coverage in `demo-backend`.
- When adding a new module, register it in `settings.gradle.kts` (`include(...)`) and add a `build.gradle.kts` mirroring existing starters (apply `java-library`, Kotlin plugins, and `io.spring.dependency-management`; shared config lives in the root `build.gradle.kts` `subprojects` block).

