package com.jiangtj.micro.sql.jooq.gen;

import lombok.Getter;
import lombok.Setter;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * JOOQ 代码生成器的辅助工具类。
 */
public class GenerateHelper {

    /** 数据库连接 URL */
    static String url = null;
    /** 数据库用户名 */
    @Setter
    static String username = null;
    /** 数据库密码 */
    @Setter
    static String password = null;
    /** 数据库驱动类名 */
    @Setter
    static String driver = null;
    /** 是否生成 POJO 类型引用方法，默认为 false */
    @Setter
    @Getter
    static boolean isGeneratePojoTypeRef = false;
    /** 是否生成分页查询方法，默认为 true */
    @Setter
    @Getter
    static boolean isGeneratePageFetch = true;
    @Setter
    @Getter
    static boolean isGeneratePojoWithLombok = true;
    @Setter
    @Getter
    static boolean isGeneratePojoWithLombokBuilder = false;

    /**
     * 使用 Spring 的数据源配置初始化生成器配置。
     *
     * @param properties Spring 数据源配置对象
     */
    public static void init(DataSourceProperties properties) {
        init(properties.getUrl(), properties.getUsername(), properties.getPassword());
        driver = properties.getDriverClassName();
    }

    /**
     * 使用数据库连接信息初始化生成器配置。
     *
     * @param url      数据库连接 URL
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public static void init(String url, String username, String password) {
        GenerateHelper.url = url;
        GenerateHelper.username = username;
        GenerateHelper.password = password;
        GenerateHelper.driver = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
    }

    /**
     * 从数据库 URL 中提取 schema 名称。
     *
     * 支持两种结构：
     * 1. 路径式（MySQL 等），如 jdbc:mysql://host:3306/db?param，取第一个路径段；
     * 2. H2 式，如 jdbc:h2:mem:db;MODE=MySQL;DATABASE_TO_LOWER=TRUE，
     * 其中 mem:db 是数据库名而非 schema，需按 H2 规则解析。
     *
     * @param url 数据库连接 URL
     * @return schema 名称
     */
    public static String getSchemaFromUrl(String url) {
        if (DatabaseDriver.fromJdbcUrl(url) == DatabaseDriver.H2) {
            return getH2Schema(url);
        }
        return url.split("//")[1].split("/")[1].split("\\?")[0];
    }

    /**
     * 解析 H2 URL 中的 schema。
     * <p>
     * H2 的 schema 与数据库名无关（{@code jdbc:h2:mem:demo-backend} 中 {@code demo-backend} 只是库名），
     * 因此按以下顺序确定：
     * <ol>
     *   <li>URL 参数中的 {@code SCHEMA=xxx}（显式指定，忽略大小写，支持引号包裹）；</li>
     *   <li>默认 schema {@code PUBLIC}，当 {@code DATABASE_TO_LOWER=TRUE} 时为 {@code public}。</li>
     * </ol>
     *
     * @param url H2 数据库连接 URL
     * @return schema 名称
     */
    private static String getH2Schema(String url) {
        Map<String, String> settings = new HashMap<>();
        // 形如 jdbc:h2:mem:db;KEY=VALUE;KEY=VALUE，第一个分号前是库名/路径
        String[] parts = url.split(";");
        for (int i = 1; i < parts.length; i++) {
            int index = parts[i].indexOf('=');
            if (index > 0) {
                settings.put(parts[i].substring(0, index).trim().toUpperCase(Locale.ROOT),
                    parts[i].substring(index + 1).trim());
            }
        }
        String schema = settings.get("SCHEMA");
        if (schema != null && !schema.isEmpty()) {
            // 去掉可能的引号，例如 SCHEMA="my schema"
            if (schema.length() > 1 && schema.startsWith("\"") && schema.endsWith("\"")) {
                return schema.substring(1, schema.length() - 1);
            }
            return schema;
        }
        return "TRUE".equalsIgnoreCase(settings.get("DATABASE_TO_LOWER")) ? "public" : "PUBLIC";
    }

    /**
     * 获取 JDBC 配置对象。
     *
     * @return JOOQ 的 JDBC 配置对象
     */
    public static Jdbc getJdbc() {
        return new Jdbc().withDriver(driver).withUrl(url).withUser(username).withPassword(password);
    }

    /**
     * 获取数据库配置对象。
     *
     * @param pattern 表名匹配模式
     * @return JOOQ 的数据库配置对象
     */
    public static Database getDatabase(String pattern) {
        return new Database().withIncludes(pattern).withInputSchema(getSchemaFromUrl(url));
    }

    /**
     * 获取代码生成目标配置对象。
     *
     * @param packageName 生成的代码包名
     * @return JOOQ 的代码生成目标配置对象
     */
    public static Target getTarget(String packageName) {
        return new Target().withPackageName(packageName).withDirectory("src/main/java");
    }
}
