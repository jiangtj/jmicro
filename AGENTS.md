# AGENTS Guide for `jmicro`

## Big Picture (Monorepo + Module Boundaries)
- This repo is a Spring Boot 4 multi-module monorepo (`pom.xml`) with two active profiles by default: `server` (demo apps) and `lib` (reusable starters/libs).
- Core library modules live under `micro-*`; runnable demos are `demo-backend` (Servlet) and `demo-reactive` (WebFlux), both configured for port `17001` (`demo-*/src/main/resources/application.properties`).
- Dependency version alignment is centralized in `micro-dependencies/pom.xml` and imported by consumers as BOM.
- `micro-spring-boot-starter` is the opinionated entry point for app defaults (exception handling + web filter wiring), while specialized features stay in dedicated starters (`micro-auth`, `micro-auth-oidc`, `micro-pic-upload-starter`, `micro-flyway-starter`).

## Architecture Patterns You Should Follow
- Prefer Spring Boot auto-configuration extension points over direct wiring: each starter registers via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
- Auth is intentionally lightweight and filter/AOP-centered (not Spring Security-first): see `micro-auth/README.md` and `micro-auth/src/main/java/com/jiangtj/micro/auth/AuthAutoConfiguration.java`.
- Servlet/reactive parity is a design goal: many features provide both sides (`micro-auth` has servlet + reactive auto-config entries; demos implement the same behavior in both stacks).
- Shared JSON behavior is initialized in auto-config (`micro-web/src/main/java/com/jiangtj/micro/web/JMicroCommonAutoConfiguration.java` -> `JsonUtils.init(mapper)`).

## Config and Integration Hotspots
- Property namespaces are module-specific and important: `jmicro.auth.*`, `jmicro.jwt.*`, `micro.flyway.*`, `micro.pic.upload.*`, plus provider namespaces (`minio.*`, `ali.oss.*`, `hw.obs.*`, `easyimages.api.*`).
- OIDC/JWT support is in `micro-auth-oidc`; default OIDC server support is opt-in and documented in `micro-auth-oidc/README.md`.
- Demo auth integration depends on Casdoor (`docker-compose.yml`, root `README.md` Casdoor setup, and `demo-front/src/main.ts`).
- Frontend talks directly to backend base URL `http://localhost:17001` (`demo-front/src/main.ts`) and attaches bearer tokens in `demo-front/src/core/token.ts`.

## Developer Workflows (Project-Specific)
- Local library bootstrap (recommended by maintainers):
  - `mvn install -P lib`
- Build/test all modules from root (profiles are active by default in parent POM):
  - `mvn test`
- Run one demo backend at a time (both default to same port):
  - `mvn -pl demo-backend spring-boot:run`
  - `mvn -pl demo-reactive spring-boot:run`
- Frontend workflow (inside `demo-front`): use scripts in `demo-front/package.json` (`dev`, `build`, `test:unit`, `lint`).

## Testing and Coding Conventions Seen in Repo
- Integration tests commonly use `@JMicroTest` from `micro-test` (`micro-test/src/main/java/com/jiangtj/micro/test/JMicroTest.java`) and auth helpers like `@WithMockUser` (see `micro-auth/README.md`).
- Nullness is explicit via JSpecify `@NullMarked` package-level defaults across modules (search `**/package-info.java`).
- Java + Kotlin coexist in modules; keep interop-friendly APIs and avoid introducing framework patterns that bypass current starter auto-config design.
- When adding new starter behavior, mirror existing pattern: properties class + auto-config + `AutoConfiguration.imports` registration + demo/test coverage in either `demo-backend` or `demo-reactive`.

