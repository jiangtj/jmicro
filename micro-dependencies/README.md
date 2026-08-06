# J Micro Dependencies (BOM)

`micro-dependencies` 是一个 Gradle `java-platform` 项目，等价于 Maven 的 BOM（Bill of Materials）。它集中约束所有 `micro-*` 发布模块的版本，供使用方通过依赖管理 `import` 实现版本对齐，避免各模块版本冲突。

## 它做了什么

在 [build.gradle.kts](./build.gradle.kts) 中，所有库模块以 `api(project(...))` 形式登记为 `constraints`：

```kotlin
dependencies {
    constraints {
        api(project(":micro-common"))
        api(project(":micro-web"))
        api(project(":micro-auth"))
        api(project(":micro-spring-boot-starter"))
        api(project(":micro-sql-jooq"))
        api(project(":micro-test"))
        api(project(":micro-pic-upload-starter"))
        api(project(":micro-business"))
    }
}
```

发布后生成一个 `micro-dependencies-<version>.pom`，被消费方 `import` 后即可在使用各模块时**省略版本号**。

## 在 Maven 中使用

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.jiangtj.micro</groupId>
            <artifactId>micro-dependencies</artifactId>
            <version>${last-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 在 Gradle 中使用

```kotlin
dependencies {
    implementation(platform("com.jiangtj.micro:micro-dependencies:${lastVersion}"))
}
```

## 注意事项

- 本模块**仅做版本对齐**，不引入任何实际代码依赖。
- `demo-*` 应用不在对齐范围内（非发布模块）。
- 第三方依赖（Spring Boot、Kotlin 等）的版本由根 `build.gradle.kts` 的 `io.spring.dependency-management` 插件统一管理，不在此 BOM 中重复声明。

> 上层用法见 [根 README](../README.md)。
