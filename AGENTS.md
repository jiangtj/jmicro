# AGENTS Guide for `jmicro`

## Big Picture (Monorepo + Module Boundaries)
- This repo is a Spring Boot 4 multi-module monorepo built with Gradle (Kotlin DSL: `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`). `micro-dependencies` is a `java-platform` project (Gradle BOM equivalent).
- Core library modules live under `micro-*`; runnable demos are `demo-backend` (Servlet) and `demo-reactive` (WebFlux), both configured for port `17001` (`demo-*/src/main/resources/application.properties`).
- Dependency version alignment is centralized in the `micro-dependencies` java-platform project and consumed by other modules; the Spring Boot BOM is imported via the `io.spring.dependency-management` plugin configured in the root `build.gradle.kts`.
- `micro-spring-boot-starter` is the opinionated entry point for app defaults (exception handling + web filter wiring), while specialized features stay in dedicated starters (`micro-auth`, `micro-business`). Flyway support now lives in `micro-business` as an optional dependency, alongside the picture-upload capability (merged from the former `micro-pic-upload-starter`, package `com.jiangtj.micro.business.pic`).
- OIDC/JWT 核心能力（包 `com.jiangtj.micro.auth.oidc`）位于 `micro-auth` 模块；可选的 OIDC Server（Cas）能力位于 `micro-business` 模块（包 `com.jiangtj.micro.business.oidc.cas`，默认关闭），并依赖 `micro-auth` 提供的 OIDC 基础能力。

## Architecture Patterns You Should Follow
- Prefer Spring Boot auto-configuration extension points over direct wiring: each starter registers via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- Auth is intentionally lightweight and filter/AOP-centered (not Spring Security-first): see `micro-auth/README.md` and `micro-auth/src/main/java/com/jiangtj/micro/auth/AuthAutoConfiguration.java`.
- Servlet/reactive parity is a design goal: many features provide both sides (`micro-auth` has servlet + reactive auto-config entries; demos implement the same behavior in both stacks).
- Shared JSON behavior is initialized in auto-config (`micro-web/src/main/java/com/jiangtj/micro/web/JMicroCommonAutoConfiguration.java` -> `JsonUtils.init(mapper)`).

## Config and Integration Hotspots
- Property namespaces are module-specific and important: `jmicro.auth.*`, `jmicro.jwt.*`, `jmicro.flyway.*`, `jmicro.pic.upload.*` (picture upload, lives in `micro-business`), plus provider namespaces (`minio.*`, `ali.oss.*`, `hw.obs.*`, `easyimages.api.*`).
- OIDC/JWT support is in `micro-auth` (package `com.jiangtj.micro.auth.oidc`); the optional OIDC server (Cas) support is in `micro-business` (package `com.jiangtj.micro.business.oidc.cas`) and is opt-in.
- Demo auth integration depends on Casdoor (`docker-compose.yml` and root `README.md` Casdoor setup).

## Developer Workflows (Project-Specific)
- Local library install (recommended by maintainers, publishes to Maven Local):
  - `./gradlew publishToMavenLocal`
- Build/test all modules from root:
  - `./gradlew test`
- Run one demo backend at a time (both default to same port):
  - `./gradlew :demo-backend:bootRun`
  - `./gradlew :demo-reactive:bootRun`

## Testing and Coding Conventions Seen in Repo
- Integration tests commonly use `@JMicroTest` from `micro-test` (`micro-test/src/main/java/com/jiangtj/micro/test/JMicroTest.java`) and auth helpers like `@WithMockUser` (see `micro-auth/README.md`).
- Nullness is explicit via JSpecify `@NullMarked` package-level defaults across modules (search `**/package-info.java`).
- Java + Kotlin coexist in modules; keep interop-friendly APIs and avoid introducing framework patterns that bypass current starter auto-config design.
- When adding new starter behavior, mirror existing pattern: properties class + auto-config + `AutoConfiguration.imports` registration + demo/test coverage in either `demo-backend` or `demo-reactive`.
- When adding a new module, register it in `settings.gradle.kts` (`include(...)`) and add a `build.gradle.kts` mirroring existing starters (apply `java-library`, Kotlin plugins, and `io.spring.dependency-management`; shared config lives in the root `build.gradle.kts` `subprojects` block).

